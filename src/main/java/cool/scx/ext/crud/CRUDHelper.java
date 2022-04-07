package cool.scx.ext.crud;

import com.google.common.collect.ArrayListMultimap;
import cool.scx.ScxContext;
import cool.scx.base.BaseModel;
import cool.scx.base.BaseModelService;
import cool.scx.ext.crud.annotation.NoCRUD;
import cool.scx.ext.crud.exception.UnknownCRUDModelException;
import cool.scx.http.exception.impl.BadRequestException;
import cool.scx.util.ObjectUtils;
import cool.scx.util.StringUtils;
import cool.scx.util.ansi.Ansi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

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
     * @param entityMap      a
     * @param baseModelClass a
     * @return a
     */
    public static <B extends BaseModel> B mapToBaseModel(Map<String, Object> entityMap, Class<B> baseModelClass) {
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
    public static Class<BaseModel> getBaseModelClass(String baseModelName) throws UnknownCRUDModelException {
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

}
