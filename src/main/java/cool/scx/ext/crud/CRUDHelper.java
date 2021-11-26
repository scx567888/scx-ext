package cool.scx.ext.crud;

import com.google.common.collect.ArrayListMultimap;
import cool.scx.ScxContext;
import cool.scx.annotation.NoColumn;
import cool.scx.base.BaseModel;
import cool.scx.base.BaseService;
import cool.scx.bo.Query;
import cool.scx.exception.impl.BadRequestException;
import cool.scx.exception.impl.CustomHttpException;
import cool.scx.sql.OrderByType;
import cool.scx.sql.WhereType;
import cool.scx.util.ObjectUtils;
import cool.scx.util.StringUtils;
import cool.scx.util.ansi.Ansi;
import cool.scx.vo.Json;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class CRUDHelper {

    /**
     * scx bean 名称 和 class 对应映射
     */
    private static final Map<String, Class<BaseModel>> BASE_MODEL_NAME_CLASS_MAPPING = initBaseModelNameClassMapping();

    /**
     * scx bean 的 class 和对应的 scxService 的 class 的映射
     */
    private static final Map<Class<BaseModel>, Class<BaseService<BaseModel>>> BASE_MODEL_CLASS_BASE_SERVICE_CLASS_MAPPING = initBaseModelClassBaseServiceClassMapping();

    /**
     * 缓存
     */
    private static final Map<Class<BaseModel>, BaseService<BaseModel>> BASE_MODEL_CLASS_BASE_SERVICE_CACHE = new HashMap<>();

    /**
     * 获取 service
     *
     * @param modelName model 名称
     * @return service
     */
    public static BaseService<BaseModel> getBaseService(String modelName) {
        //先通过 modelName 获取 class
        var baseModelClass = getBaseModelClassByName(modelName);
        try {
            // 从缓存中获取 baseService
            var baseService = BASE_MODEL_CLASS_BASE_SERVICE_CACHE.get(baseModelClass);
            // 缓存未命中
            if (baseService == null) {
                var baseServiceClass = BASE_MODEL_CLASS_BASE_SERVICE_CLASS_MAPPING.get(baseModelClass);
                //查看映射中是否存在 存在 则通过 spring 获取 不存在则通过 手动 new
                baseService = baseServiceClass != null ? ScxContext.beanFactory().getBean(baseServiceClass) : new BaseService<>(baseModelClass);
                //添加到缓存中
                BASE_MODEL_CLASS_BASE_SERVICE_CACHE.put(baseModelClass, baseService);
            }
            return baseService;
        } catch (Exception e) {
            e.printStackTrace();
            throw new UnknownCRUDModelException(modelName);
        }
    }

    /**
     * 获取 baseModel
     *
     * @param entityMap e
     * @return a
     */
    public static BaseModel mapToBaseModel(Map<String, Object> entityMap, String baseModelName) {
        var baseModelClass = getBaseModelClassByName(baseModelName);
        try {
            return ObjectUtils.convertValue(entityMap, baseModelClass);
        } catch (Exception e) {
            e.printStackTrace();
            //这里一般就是 参数转换错误
            throw new BadRequestException(e);
        }
    }

    /**
     * <p>getClassByName.</p>
     *
     * @param baseModelName a {@link java.lang.String} object.
     * @return a {@link java.lang.Class} object.
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
     * @param limit           l
     * @param page            p
     * @param orderByBodyList or
     * @param whereBodyList   wh
     * @return q
     */
    public static Query getQuery(Class<? extends BaseModel> modelClass, Integer limit, Integer page, List<CRUDOrderByBody> orderByBodyList, List<CRUDWhereBody> whereBodyList) throws CustomHttpException {
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
                    query.addOrderBy(orderByBody.fieldName, sortType);
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
                        query.where().add(crudWhereBody.fieldName, whereType);
                    } else if (whereType.paramSize() == 1) {
                        query.where().add(crudWhereBody.fieldName, whereType, crudWhereBody.value1);
                    } else if (whereType.paramSize() == 2) {
                        query.where().add(crudWhereBody.fieldName, whereType, crudWhereBody.value1, crudWhereBody.value2);
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
     * @throws CustomHttpException c
     */
    public static void checkFieldName(Class<?> modelClass, String fieldName) throws CustomHttpException {
        try {
            var field = modelClass.getField(fieldName);
            if (field.isAnnotationPresent(NoColumn.class)) {
                throw new CustomHttpException(ctx -> Json.fail("unknown-field-name").put("field-name", fieldName).handle(ctx));
            }
        } catch (Exception e) {
            throw new CustomHttpException(ctx -> Json.fail("unknown-field-name").put("field-name", fieldName).handle(ctx));
        }
    }

    /**
     * 检查 where 类型
     *
     * @param fieldName    f
     * @param strWhereType s
     * @return s
     * @throws CustomHttpException s
     */
    public static WhereType checkWhereType(String fieldName, String strWhereType) throws CustomHttpException {
        try {
            return WhereType.valueOf(strWhereType.toUpperCase());
        } catch (Exception ignored) {
            throw new CustomHttpException(ctx -> Json.fail("unknown-where-type").put("field-name", fieldName).put("where-type", strWhereType).handle(ctx));
        }
    }

    /**
     * a
     *
     * @param fieldName   a
     * @param strSortType a
     * @return a
     * @throws CustomHttpException a
     */
    public static OrderByType checkSortType(String fieldName, String strSortType) throws CustomHttpException {
        try {
            return OrderByType.valueOf(strSortType.toUpperCase());
        } catch (Exception ignored) {
            throw new CustomHttpException(ctx -> Json.fail("unknown-sort-type").put("field-name", fieldName).put("sort-type", strSortType).handle(ctx));
        }
    }

    /**
     * 检查 whereBody 参数数量是否合法
     *
     * @param fieldName f
     * @param whereType w
     * @param value1    v
     * @param value2    v
     * @throws CustomHttpException v
     */
    public static void checkWhereBodyParametersSize(String fieldName, WhereType whereType, Object value1, Object value2) throws CustomHttpException {
        AtomicInteger paramSize = new AtomicInteger();
        if (value1 != null) {
            paramSize.set(paramSize.get() + 1);
        }
        if (value2 != null) {
            paramSize.set(paramSize.get() + 1);
        }

        if (whereType.paramSize() != paramSize.get()) {
            throw new CustomHttpException(ctx -> Json.fail("where-body-parameters-size-error")
                    .put("field-name", fieldName)
                    .put("where-type", whereType)
                    .put("need-parameters-size", whereType.paramSize())
                    .put("got-parameters-size", paramSize.get())
                    .handle(ctx));
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Class<BaseModel>> initBaseModelNameClassMapping() {
        var tempMap = new HashMap<String, Class<BaseModel>>();
        for (var scxModuleInfo : ScxContext.scxModuleInfos()) {
            for (var c : scxModuleInfo.scxModelClassList()) {
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

    @SuppressWarnings("unchecked")
    private static Map<Class<BaseModel>, Class<BaseService<BaseModel>>> initBaseModelClassBaseServiceClassMapping() {
        // 因为一个 BaseModel 可能由多个 BaseService 的实现 这里使用 HashSetValuedHashMap 存储
        ArrayListMultimap<Class<BaseModel>, Class<BaseService<BaseModel>>> classClassHashSetValuedHashMap = ArrayListMultimap.create();
        // baseModelClassList
        var baseModelClassList = CRUDHelper.BASE_MODEL_NAME_CLASS_MAPPING.values();
        //循环读取
        for (var allModule : ScxContext.scxModuleInfos()) {
            for (var c : allModule.scxServiceClassList()) {
                //这里获取 泛型
                var typeArguments = ((ParameterizedType) c.getGenericSuperclass()).getActualTypeArguments();

                var baseModelClass = (Class<BaseModel>) typeArguments[0];
                //我们只需要 包含在 baseModelClassList 中的 baseService
                if (baseModelClassList.contains(baseModelClass)) {
                    classClassHashSetValuedHashMap.put(baseModelClass, (Class<BaseService<BaseModel>>) c);
                }
            }
        }

        //将 classClassHashSetValuedHashMap 转换为 普通的 map 并且 对于拥有多个 BaseService 实现的数据进行 警告提示
        var tempMap = new HashMap<Class<BaseModel>, Class<BaseService<BaseModel>>>();

        for (var key : classClassHashSetValuedHashMap.keySet()) {
            var classes = classClassHashSetValuedHashMap.get(key);
            var lastThisBaseModelClassBaseServiceClass = classes.get(classes.size() - 1);
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
                Ansi.out().brightRed("检测到针对 " + key.getName() + " 的多个 BaseService 实现 , 已采用最后一个 [" + lastThisBaseModelClassBaseServiceClass.getName() + "] ,").
                        brightYellow(" 其余的 BaseService 实现 " + sb).println();
            }
            tempMap.put(key, lastThisBaseModelClassBaseServiceClass);
        }

        return tempMap;
    }

}
