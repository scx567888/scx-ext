package cool.scx.ext.crud;

import cool.scx.core.ScxContext;
import cool.scx.enumeration.HttpMethod;
import cool.scx.ext.crud.exception.CRUDApiAlreadyDisableException;
import cool.scx.mvc.annotation.FromBody;
import cool.scx.mvc.annotation.FromPath;
import cool.scx.mvc.annotation.ScxRoute;
import cool.scx.mvc.vo.BaseVo;
import cool.scx.mvc.vo.Result;

import java.util.Map;

import static cool.scx.ext.crud.CRUDApiType.*;

/**
 * 通用 Crud的 controller
 *
 * @author scx567888
 * @version 1.0.10
 */
@ScxRoute("api/crud")
public class CRUDController {

    private final CRUDHandler crudHandler;

    /**
     * a
     */
    public CRUDController() {
        var crudHandlerClass = ScxContext.findScxModule(CRUDModule.class).crudHandlerClass();
        this.crudHandler = ScxContext.getBean(crudHandlerClass);
    }

    /**
     * <p>checkHasThisApi.</p>
     *
     * @param modelName a {@link java.lang.String} object
     * @param apiType   a {@link cool.scx.ext.crud.CRUDApiType} object
     */
    private static void checkHasThisApi(String modelName, CRUDApiType apiType) {
        var crudApiInfo = CRUDHelper.getCRUDApiInfo(modelName);
        var hasThisApi = crudApiInfo.hasThisApi(apiType);
        if (!hasThisApi) {
            throw new CRUDApiAlreadyDisableException(modelName, apiType);
        }
    }

    /**
     * 列表查询
     *
     * @param modelName     a {@link java.lang.String} object.
     * @param crudListParam a
     * @return a
     */
    @ScxRoute(value = ":modelName/list", methods = HttpMethod.POST)
    public BaseVo list(@FromPath String modelName, CRUDListParam crudListParam) {
        checkHasThisApi(modelName, LIST);
        var crudListResult = crudHandler.list(modelName, crudListParam);
        return Result.ok().put("items", crudListResult.list()).put("total", crudListResult.total());
    }

    /**
     * 获取详细信息
     *
     * @param modelName a {@link java.lang.String} object.
     * @param id        a {@link java.lang.Long} object.
     * @return a
     */
    @ScxRoute(value = ":modelName/:id", methods = HttpMethod.GET)
    public BaseVo info(@FromPath String modelName, @FromPath Long id) {
        checkHasThisApi(modelName, INFO);
        var info = crudHandler.info(modelName, id);
        return Result.ok(info);
    }

    /**
     * 保存
     *
     * @param modelName a {@link java.lang.String} object.
     * @param saveModel a {@link java.util.Map} object.
     * @return a
     */
    @ScxRoute(value = ":modelName", methods = HttpMethod.POST)
    public BaseVo add(@FromPath String modelName, @FromBody(useAllBody = true) Map<String, Object> saveModel) {
        checkHasThisApi(modelName, ADD);
        var savedModel = crudHandler.add(modelName, saveModel);
        return Result.ok(savedModel);
    }

    /**
     * 更新
     *
     * @param modelName       a {@link java.lang.String} object.
     * @param crudUpdateParam a {@link java.util.Map} object.
     * @return a
     */
    @ScxRoute(value = ":modelName", methods = HttpMethod.PUT)
    public BaseVo update(@FromPath String modelName, CRUDUpdateParam crudUpdateParam) {
        checkHasThisApi(modelName, UPDATE);
        var updatedModel = crudHandler.update(modelName, crudUpdateParam);
        return Result.ok(updatedModel);
    }

    /**
     * 删除
     *
     * @param modelName a
     * @param id        a
     * @return j
     */
    @ScxRoute(value = ":modelName/:id", methods = HttpMethod.DELETE)
    public BaseVo delete(@FromPath String modelName, @FromPath Long id) {
        checkHasThisApi(modelName, DELETE);
        var b = crudHandler.delete(modelName, id);
        return b ? Result.ok() : Result.fail();
    }

    /**
     * 批量删除
     *
     * @param modelName a {@link java.lang.String} object.
     * @param deleteIDs a {@link java.util.Map} object.
     * @return a
     */
    @ScxRoute(value = ":modelName/batch-delete", methods = HttpMethod.DELETE)
    public BaseVo batchDelete(@FromPath String modelName, @FromBody long[] deleteIDs) {
        checkHasThisApi(modelName, BATCH_DELETE);
        var deletedCount = crudHandler.batchDelete(modelName, deleteIDs);
        return Result.ok().put("deletedCount", deletedCount);
    }

    /**
     * 校验唯一性
     *
     * @param modelName a
     * @param fieldName a
     * @param value     a
     * @param id        a
     * @return a
     */
    @ScxRoute(value = ":modelName/check-unique/:fieldName", methods = HttpMethod.POST)
    public BaseVo checkUnique(@FromPath String modelName, @FromPath String fieldName, @FromBody Object value, @FromBody(required = false) Long id) {
        checkHasThisApi(modelName, CHECK_UNIQUE);
        var isUnique = crudHandler.checkUnique(modelName, fieldName, value, id);
        return Result.ok().put("isUnique", isUnique);
    }

}
