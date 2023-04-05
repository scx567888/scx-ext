package cool.scx.ext.crud;

import cool.scx.core.ScxContext;
import cool.scx.core.ScxHelper;
import cool.scx.core.base.BaseModel;
import cool.scx.core.base.BaseModelService;
import cool.scx.data.annotation.NoColumn;
import cool.scx.ext.crud.annotation.UseCRUDApi;
import cool.scx.ext.crud.exception.UnknownCRUDModelException;
import cool.scx.ext.crud.exception.UnknownFieldNameException;
import cool.scx.mvc.exception.BadRequestException;
import cool.scx.mvc.exception.NotFoundException;
import cool.scx.util.MultiMap;
import cool.scx.util.ObjectUtils;
import cool.scx.util.StringUtils;
import cool.scx.util.ansi.Ansi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * a
 *
 * @author scx567888
 * @version 1.7.7
 */
public final class CRUDHelper {

    /**
     * scx bean 名称 和 CRUDApiInfo 对应映射 (此处公开此字段 保证外界在特殊情况下能够动态修改 某些 crudApi 的处理情况)
     */
    public static final Map<String, CRUDApiInfo> BASE_MODEL_NAME_CRUD_API_INFO_MAPPING = initBaseModelNameCRUDApiInfoMapping();

    /**
     * a
     */
    private static final Logger logger = LoggerFactory.getLogger(CRUDHelper.class);

    /**
     * 忽略的分割符
     */
    private static final Pattern IgnoredSeparatorRegex = Pattern.compile("[-_]");

    /**
     * scx bean 的 class 和对应的 scxService 的 class 的映射
     */
    private static final Map<Class<BaseModel>, Class<BaseModelService<BaseModel>>> BASE_MODEL_CLASS_BASE_SERVICE_CLASS_MAPPING = initBaseModelClassBaseModelServiceClassMapping();

    /**
     * 缓存
     */
    private static final Map<Class<BaseModel>, BaseModelService<BaseModel>> BASE_MODEL_CLASS_BASE_MODEL_SERVICE_CACHE = new HashMap<>();

    /**
     * a
     *
     * @param baseModelClass a
     * @return a
     */
    public static BaseModelService<BaseModel> getBaseModelService(Class<BaseModel> baseModelClass) {
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
    }

    /**
     * 获取 baseModel
     *
     * @param map            a
     * @param baseModelClass a
     * @param <B>            b
     * @return a
     */
    public static <B extends BaseModel> B mapToBaseModel(Map<String, Object> map, Class<B> baseModelClass) {
        try {
            return ObjectUtils.convertValue(map, baseModelClass, ObjectUtils.Option.IGNORE_JSON_IGNORE);
        } catch (Exception e) {
            logger.error("将 Map 转换为 BaseModel 时发生异常 : ", e);
            //这里一般就是 参数转换错误
            throw new BadRequestException(e);
        }
    }

    public static CRUDApiInfo getCRUDApiInfo(String baseModelName) throws NotFoundException {
        if (StringUtils.isBlank(baseModelName)) {
            throw new UnknownCRUDModelException(baseModelName);
        }
        var finalBaseModelName = normalizeModelName(baseModelName);
        var baseModelClass = BASE_MODEL_NAME_CRUD_API_INFO_MAPPING.get(finalBaseModelName);
        if (baseModelClass == null) {
            throw new UnknownCRUDModelException(baseModelName);
        }
        return baseModelClass;
    }

    /**
     * <p>initBaseModelNameClassMapping.</p>
     *
     * @return a {@link java.util.Map} object
     */
    @SuppressWarnings("unchecked")
    private static HashMap<String, CRUDApiInfo> initBaseModelNameCRUDApiInfoMapping() {
        var tempMap = new HashMap<String, CRUDApiInfo>();
        var classList = Arrays.stream(ScxContext.scxModules())
                .flatMap(c -> c.classList().stream())
                .filter(ScxHelper::isScxBaseModelClass)
                .toList();
        for (var c : classList) {
            var useCRUDApiAnnotation = c.getAnnotation(UseCRUDApi.class);
            if (useCRUDApiAnnotation != null) {
                var crudApiInfo = new CRUDApiInfo(useCRUDApiAnnotation, (Class<BaseModel>) c);
                //获取上一个重名的
                var last = tempMap.get(crudApiInfo.baseModelName);
                tempMap.put(crudApiInfo.baseModelName, crudApiInfo);
                if (last != null) {
                    Ansi.out().brightRed("检测到重复名称的 BaseModel ").brightYellow("[" + last.baseModelClass.getName() + "] ").blue("[" + c.getName() + "]").brightRed(" 可能会导致根据名称调用时意义不明确 !!! 建议修改 !!!").println();
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
        var classClassHashSetValuedHashMap = new MultiMap<Class<BaseModel>, Class<BaseModelService<BaseModel>>>();
        // baseModelClassList
        var baseModelClassList = CRUDHelper.BASE_MODEL_NAME_CRUD_API_INFO_MAPPING.values().stream().map(c -> c.baseModelClass).toList();
        //循环读取
        var classList = Arrays.stream(ScxContext.scxModules())
                .flatMap(c -> c.classList().stream())
                .filter(ScxHelper::isScxBaseModelServiceClass)
                .toList();
        for (var c : classList) {
            //这里获取 泛型
            var typeArguments = ((ParameterizedType) c.getGenericSuperclass()).getActualTypeArguments();

            var baseModelClass = (Class<BaseModel>) typeArguments[0];
            //我们只需要 包含在 baseModelClassList 中的 baseModelService
            if (baseModelClassList.contains(baseModelClass)) {
                classClassHashSetValuedHashMap.put(baseModelClass, (Class<BaseModelService<BaseModel>>) c);
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
                for (int i = 0; i < classes.size() - 1; i = i + 1) {
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
     * 检查 fieldName 是否合法
     *
     * @param modelClass m
     * @param fieldName  f
     * @return a {@link java.lang.String} object
     * @throws cool.scx.ext.crud.exception.UnknownFieldNameException c
     */
    public static String checkFieldName(Class<?> modelClass, String fieldName) throws UnknownFieldNameException {
        try {
            var field = modelClass.getField(fieldName);
            if (field.isAnnotationPresent(NoColumn.class)) {
                throw new UnknownFieldNameException(fieldName);
            }
        } catch (Exception e) {
            throw new UnknownFieldNameException(fieldName);
        }
        return fieldName;
    }

    /**
     * 标准化 modelName
     *
     * @param baseModelName a
     * @return a
     */
    public static String normalizeModelName(String baseModelName) {
        return IgnoredSeparatorRegex.matcher(baseModelName).replaceAll("").toLowerCase();
    }

}
