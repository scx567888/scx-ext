package cool.scx.ext.crud;

import com.google.common.collect.ArrayListMultimap;
import cool.scx.ScxContext;
import cool.scx.annotation.NoColumn;
import cool.scx.base.*;
import cool.scx.dao.ScxDaoTableInfo;
import cool.scx.ext.crud.annotation.NoCRUD;
import cool.scx.ext.crud.exception.*;
import cool.scx.http.exception.impl.BadRequestException;
import cool.scx.sql.order_by.OrderByType;
import cool.scx.sql.where.WhereType;
import cool.scx.util.ObjectUtils;
import cool.scx.util.StringUtils;
import cool.scx.util.ansi.Ansi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * a
 *
 * @author scx567888
 * @version 1.7.7
 */
public final class CRUDHelper {

    private static final Logger logger = LoggerFactory.getLogger(CRUDHelper.class);

    /**
     * scx bean 名称 和 class 对应映射
     */
    private static final Map<String, Class<BaseModel>> BASE_MODEL_NAME_CLASS_MAPPING = initBaseModelNameClassMapping();

    /**
     * scx bean 的 class 和对应的 scxService 的 class 的映射
     */
    private static final Map<Class<BaseModel>, Class<BaseModelService<BaseModel>>> BASE_MODEL_CLASS_BASE_SERVICE_CLASS_MAPPING = initBaseModelClassBaseModelServiceClassMapping();

    /**
     * 缓存
     */
    private static final Map<Class<BaseModel>, BaseModelService<BaseModel>> BASE_MODEL_CLASS_BASE_MODEL_SERVICE_CACHE = new HashMap<>();

    /**
     * 获取 service
     *
     * @param modelName model 名称
     * @return service
     */
    public static BaseModelService<BaseModel> getBaseModelService(String modelName) {
        //先通过 modelName 获取 class
        var baseModelClass = getBaseModelClassByName(modelName);
        try {
            // 从缓存中获取 baseModelService
            var baseModelService = BASE_MODEL_CLASS_BASE_MODEL_SERVICE_CACHE.get(baseModelClass);
            // 缓存未命中
            if (baseModelService == null) {
                var baseModelServiceClass = BASE_MODEL_CLASS_BASE_SERVICE_CLASS_MAPPING.get(baseModelClass);
                //查看映射中是否存在 存在 则通过 spring 获取 不存在则通过 手动 new
                baseModelService = baseModelServiceClass != null ? ScxContext.getBean(baseModelServiceClass) : new BaseModelService<>(baseModelClass);
                //添加到缓存中
                BASE_MODEL_CLASS_BASE_MODEL_SERVICE_CACHE.put(baseModelClass, baseModelService);
            }
            return baseModelService;
        } catch (Exception e) {
            logger.error("获取 BaseModelService 时发生异常 : ", e);
            throw new UnknownCRUDModelException(modelName);
        }
    }

    /**
     * 获取 baseModel
     *
     * @param entityMap     a
     * @param baseModelName a
     * @return a
     */
    public static BaseModel mapToBaseModel(Map<String, Object> entityMap, String baseModelName) {
        var baseModelClass = getBaseModelClassByName(baseModelName);
        try {
            return ObjectUtils.convertValue(entityMap, baseModelClass);
        } catch (Exception e) {
            logger.error("将 Map 转换为 BaseModel 时发生异常 : ", e);
            //这里一般就是 参数转换错误
            throw new BadRequestException(e);
        }
    }

    /**
     * <p>getClassByName.</p>
     *
     * @param baseModelName a {@link java.lang.String} object.
     * @return a {@link java.lang.Class} object.
     * @throws cool.scx.ext.crud.exception.UnknownCRUDModelException if any.
     */
    public static Class<BaseModel> getBaseModelClassByName(String baseModelName) throws UnknownCRUDModelException {
        if (StringUtils.isBlank(baseModelName)) {
            throw new UnknownCRUDModelException(baseModelName);
        }
        var baseModelClass = BASE_MODEL_NAME_CLASS_MAPPING.get(baseModelName.toLowerCase());
        if (baseModelClass == null) {
            throw new UnknownCRUDModelException(baseModelName);
        }
        return baseModelClass;
    }

    /**
     * 获取 Query
     *
     * @param modelClass      a
     * @param limit           a
     * @param page            a
     * @param orderByBodyList a
     * @param whereBodyList   a
     * @return a
     * @throws cool.scx.http.exception.impl.BadRequestException a
     */
    public static Query getQuery(Class<? extends BaseModel> modelClass, Integer limit, Integer page, List<CRUDOrderByBody> orderByBodyList, List<CRUDWhereBody> whereBodyList) throws BadRequestException {
        var query = new Query();
        if (limit != null && limit >= 0) {
            if (page != null && page >= 1) {
                query.setPagination(page, limit);
            } else {
                query.setPagination(limit);
            }
        }
        if (orderByBodyList != null) {
            for (var orderByBody : orderByBodyList) {
                if (orderByBody.fieldName != null && orderByBody.sortType != null) {
                    //校验 fieldName 是否正确
                    checkFieldName(modelClass, orderByBody.fieldName);
                    //检查 sortType 是否正确
                    var sortType = checkSortType(orderByBody.fieldName, orderByBody.sortType);
                    query.orderBy().add(orderByBody.fieldName, sortType);
                }
            }
        }
        if (whereBodyList != null) {
            for (var crudWhereBody : whereBodyList) {
                if (crudWhereBody.fieldName != null && crudWhereBody.whereType != null) {
                    //校验 fieldName 是否正确
                    checkFieldName(modelClass, crudWhereBody.fieldName);
                    //检查 whereType 是否正确
                    var whereType = checkWhereType(crudWhereBody.fieldName, crudWhereBody.whereType);
                    //检查参数数量是否正确
                    checkWhereBodyParametersSize(crudWhereBody.fieldName, whereType, crudWhereBody.value1, crudWhereBody.value2);
                    if (whereType.paramSize() == 0) {
                        query.where().add0(crudWhereBody.fieldName, whereType);
                    } else if (whereType.paramSize() == 1) {
                        query.where().add1(crudWhereBody.fieldName, whereType, crudWhereBody.value1);
                    } else if (whereType.paramSize() == 2) {
                        query.where().add2(crudWhereBody.fieldName, whereType, crudWhereBody.value1, crudWhereBody.value2);
                    }
                }
            }
        }
        return query;
    }

    /**
     * 检查 fieldName 是否合法
     *
     * @param modelClass m
     * @param fieldName  f
     * @throws cool.scx.ext.crud.exception.UnknownFieldName c
     */
    public static void checkFieldName(Class<?> modelClass, String fieldName) throws UnknownFieldName {
        try {
            var field = modelClass.getField(fieldName);
            if (field.isAnnotationPresent(NoColumn.class)) {
                throw new UnknownFieldName(fieldName);
            }
        } catch (Exception e) {
            throw new UnknownFieldName(fieldName);
        }
    }

    /**
     * 检查 where 类型
     *
     * @param fieldName    f
     * @param strWhereType s
     * @return s
     * @throws cool.scx.ext.crud.exception.UnknownWhereType s
     */
    public static WhereType checkWhereType(String fieldName, String strWhereType) throws UnknownWhereType {
        try {
            return WhereType.of(strWhereType);
        } catch (Exception ignored) {
            throw new UnknownWhereType(fieldName, strWhereType);
        }
    }

    /**
     * a
     *
     * @param fieldName   a
     * @param strSortType a
     * @return a
     * @throws cool.scx.ext.crud.exception.UnknownSortType a
     */
    public static OrderByType checkSortType(String fieldName, String strSortType) throws UnknownSortType {
        try {
            return OrderByType.of(strSortType);
        } catch (Exception ignored) {
            throw new UnknownSortType(fieldName, strSortType);
        }
    }

    /**
     * 检查 whereBody 参数数量是否合法
     *
     * @param fieldName f
     * @param whereType w
     * @param value1    v
     * @param value2    v
     * @throws cool.scx.ext.crud.exception.WhereBodyParametersSizeError v
     */
    public static void checkWhereBodyParametersSize(String fieldName, WhereType whereType, Object value1, Object value2) throws WhereBodyParametersSizeError {
        AtomicInteger paramSize = new AtomicInteger();
        if (value1 != null) {
            paramSize.set(paramSize.get() + 1);
        }
        if (value2 != null) {
            paramSize.set(paramSize.get() + 1);
        }

        if (whereType.paramSize() != paramSize.get()) {
            throw new WhereBodyParametersSizeError(fieldName, whereType, paramSize.get());
        }
    }

    /**
     * <p>initBaseModelNameClassMapping.</p>
     *
     * @return a {@link java.util.Map} object
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Class<BaseModel>> initBaseModelNameClassMapping() {
        var tempMap = new HashMap<String, Class<BaseModel>>();
        for (var scxModuleInfo : ScxContext.scxModuleInfos()) {
            for (var c : scxModuleInfo.scxBaseModelClassList()) {
                NoCRUD noCRUDAnnotation = c.getAnnotation(NoCRUD.class);
                if (noCRUDAnnotation != null) {
                    continue;
                }
                var className = c.getSimpleName().toLowerCase();
                var aClass = tempMap.get(className);
                tempMap.put(className, (Class<BaseModel>) c);
                if (aClass != null) {
                    Ansi.out().brightRed("检测到重复名称的 BaseModel ").brightYellow("[" + aClass.getName() + "] ").blue("[" + c.getName() + "]").brightRed(" 可能会导致根据名称调用时意义不明确 !!! 建议修改 !!!").println();
                }
            }
        }
        return tempMap;
    }

    /**
     * <p>initBaseModelClassBaseModelServiceClassMapping.</p>
     *
     * @return a {@link java.util.Map} object
     */
    @SuppressWarnings("unchecked")
    private static Map<Class<BaseModel>, Class<BaseModelService<BaseModel>>> initBaseModelClassBaseModelServiceClassMapping() {
        // 因为一个 BaseModel 可能由多个 BaseModelService 的实现 这里使用 HashSetValuedHashMap 存储
        ArrayListMultimap<Class<BaseModel>, Class<BaseModelService<BaseModel>>> classClassHashSetValuedHashMap = ArrayListMultimap.create();
        // baseModelClassList
        var baseModelClassList = CRUDHelper.BASE_MODEL_NAME_CLASS_MAPPING.values();
        //循环读取
        for (var allModule : ScxContext.scxModuleInfos()) {
            for (var c : allModule.scxBaseModelServiceClassList()) {
                //这里获取 泛型
                var typeArguments = ((ParameterizedType) c.getGenericSuperclass()).getActualTypeArguments();

                var baseModelClass = (Class<BaseModel>) typeArguments[0];
                //我们只需要 包含在 baseModelClassList 中的 baseModelService
                if (baseModelClassList.contains(baseModelClass)) {
                    classClassHashSetValuedHashMap.put(baseModelClass, (Class<BaseModelService<BaseModel>>) c);
                }
            }
        }

        //将 classClassHashSetValuedHashMap 转换为 普通的 map 并且 对于拥有多个 BaseModelService 实现的数据进行 警告提示
        var tempMap = new HashMap<Class<BaseModel>, Class<BaseModelService<BaseModel>>>();

        for (var key : classClassHashSetValuedHashMap.keySet()) {
            var classes = classClassHashSetValuedHashMap.get(key);
            var lastThisBaseModelClassBaseModelServiceClass = classes.get(classes.size() - 1);
            //有多个实现的话 打印一下 通知用户
            if (classes.size() > 1) {
                var sb = new StringBuilder();
                for (int i = 0; i < classes.size() - 1; i++) {
                    var name = classes.get(i).getName();
                    if (i == classes.size() - 2) {
                        sb.append("[").append(name).append("] ");
                    } else {
                        sb.append("[").append(name).append("],");
                    }
                }
                Ansi.out().brightRed("检测到针对 " + key.getName() + " 的多个 BaseModelService 实现 , 已采用最后一个 [" + lastThisBaseModelClassBaseModelServiceClass.getName() + "] ,").
                        brightYellow(" 其余的 BaseModelService 实现 " + sb).println();
            }
            tempMap.put(key, lastThisBaseModelClassBaseModelServiceClass);
        }

        return tempMap;
    }

    /**
     * 获取 b
     *
     * @param modelClass       a
     * @param selectFilterBody a
     * @param scxDaoTableInfo  a
     * @return a
     */
    public static SelectFilter getSelectFilter(Class<BaseModel> modelClass, CRUDSelectFilterBody selectFilterBody, ScxDaoTableInfo scxDaoTableInfo) {
        if (selectFilterBody == null) {
            return SelectFilter.ofExcluded();
        }
        var filterMode = checkFilterMode(selectFilterBody.filterMode);
        var selectFilter = switch (filterMode) {
            case EXCLUDED -> SelectFilter.ofExcluded();
            case INCLUDED -> SelectFilter.ofIncluded();
        };
        if (selectFilterBody.fieldNames != null) {
            for (var fieldName : selectFilterBody.fieldNames) {
                checkFieldName(modelClass, fieldName);
                selectFilter.add(fieldName);
            }
        }
        //防止空列查询
        if (selectFilter.filter(scxDaoTableInfo.columnInfos()).length == 0) {
            throw new EmptySelectColumn(filterMode, selectFilterBody.fieldNames);
        }
        return selectFilter;
    }

    /**
     * 检查 filterMode 是否正确
     *
     * @param filterMode f
     * @return a
     * @throws UnknownWhereType a
     */
    public static AbstractFilter.FilterMode checkFilterMode(String filterMode) throws UnknownWhereType {
        try {
            return AbstractFilter.FilterMode.of(filterMode);
        } catch (Exception ignored) {
            throw new UnknownFilterMode(filterMode);
        }
    }

}
