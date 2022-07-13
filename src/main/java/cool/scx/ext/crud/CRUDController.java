package cool.scx.ext.crud;

import cool.scx.core.ScxContext;
import cool.scx.core.annotation.FromBody;
import cool.scx.core.annotation.FromPath;
import cool.scx.core.annotation.ScxMapping;
import cool.scx.core.enumeration.HttpMethod;
import cool.scx.core.vo.BaseVo;
import cool.scx.core.vo.DataJson;
import cool.scx.core.vo.Json;
import cool.scx.ext.crud.exception.CRUDApiAlreadyDisableException;

import java.util.Map;

import static cool.scx.ext.crud.CRUDApiType.*;

/**
 * 通用 Crud的 controller
 *
 * @author scx567888
 * @version 1.0.10
 */
@ScxMapping("api/crud")
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
     * @return a {@link cool.scx.core.vo.Json} object.
     */
    @ScxMapping(value = ":modelName/list", method = HttpMethod.POST)
    public Json list(@FromPath String modelName, CRUDListParam crudListParam) {
        checkHasThisApi(modelName, LIST);
        var crudListResult = crudHandler.list(modelName, crudListParam);
        return Json.ok().put("items", crudListResult.list()).put("total", crudListResult.total());
    }

    /**
     * 获取详细信息
     *
     * @param modelName a {@link java.lang.String} object.
     * @param id        a {@link java.lang.Long} object.
     * @return a {@link cool.scx.core.vo.Json} object.
     */
    @ScxMapping(value = ":modelName/:id", method = HttpMethod.GET)
    public BaseVo info(@FromPath String modelName, @FromPath Long id) {
        checkHasThisApi(modelName, INFO);
        var info = crudHandler.info(modelName, id);
        return DataJson.ok().data(info);
    }

    /**
     * 保存
     *
     * @param modelName a {@link java.lang.String} object.
     * @param saveModel a {@link java.util.Map} object.
     * @return a {@link cool.scx.core.vo.Json} object.
     */
    @ScxMapping(value = ":modelName", method = HttpMethod.POST)
    public BaseVo add(@FromPath String modelName, @FromBody(useAllBody = true) Map<String, Object> saveModel) {
        checkHasThisApi(modelName, ADD);
        var savedModel = crudHandler.add(modelName, saveModel);
        return DataJson.ok().data(savedModel);
    }

    /**
     * 更新
     *
     * @param modelName       a {@link java.lang.String} object.
     * @param crudUpdateParam a {@link java.util.Map} object.
     * @return a {@link cool.scx.core.vo.Json} object.
     */
    @ScxMapping(value = ":modelName", method = HttpMethod.PUT)
    public BaseVo update(@FromPath String modelName, CRUDUpdateParam crudUpdateParam) {
        checkHasThisApi(modelName, UPDATE);
        var updatedModel = crudHandler.update(modelName, crudUpdateParam);
        return DataJson.ok().data(updatedModel);
    }

    /**
     * 删除
     *
     * @param modelName a
     * @param id        a
     * @return j
     */
    @ScxMapping(value = ":modelName/:id", method = HttpMethod.DELETE)
    public Json delete(@FromPath String modelName, @FromPath Long id) {
        checkHasThisApi(modelName, DELETE);
        var b = crudHandler.delete(modelName, id);
        return b ? Json.ok() : Json.fail();
    }

    /**
     * 批量删除
     *
     * @param modelName a {@link java.lang.String} object.
     * @param deleteIDs a {@link java.util.Map} object.
     * @return a {@link cool.scx.core.vo.Json} object.
     */
    @ScxMapping(value = ":modelName/batch-delete", method = HttpMethod.DELETE)
    public Json batchDelete(@FromPath String modelName, @FromBody long[] deleteIDs) {
        checkHasThisApi(modelName, BATCH_DELETE);
        var deletedCount = crudHandler.batchDelete(modelName, deleteIDs);
        return Json.ok().put("deletedCount", deletedCount);
    }

    /**
     * 撤销删除
     *
     * @param modelName a {@link java.lang.String} object.
     * @param id        a {@link java.lang.Integer} object.
     * @return a {@link cool.scx.core.vo.Json} object.
     */
    @ScxMapping(value = ":modelName/revoke-delete/:id", method = HttpMethod.GET)
    public Json revokeDelete(@FromPath String modelName, @FromPath Long id) {
        checkHasThisApi(modelName, REVOKE_DELETE);
        if (!ScxContext.coreConfig().tombstone()) {
            return Json.fail("not-used-tombstone");
        } else {
            var b = crudHandler.revokeDelete(modelName, id);
            return b ? Json.ok() : Json.fail();
        }
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
    @ScxMapping(value = ":modelName/check-unique/:fieldName", method = HttpMethod.POST)
    public Json checkUnique(@FromPath String modelName, @FromPath String fieldName, @FromBody Object value, @FromBody(required = false) Long id) {
        checkHasThisApi(modelName, CHECK_UNIQUE);
        var isUnique = crudHandler.checkUnique(modelName, fieldName, value, id);
        return Json.ok().put("isUnique", isUnique);
    }

}
