package cool.scx.ext.crud;

import cool.scx.ScxContext;
import cool.scx.annotation.FromBody;
import cool.scx.annotation.FromPath;
import cool.scx.annotation.ScxMapping;
import cool.scx.enumeration.HttpMethod;
import cool.scx.exception.HttpRequestException;
import cool.scx.vo.Json;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 通用 Crud的 controller
 *
 * @author scx567888
 * @version 1.0.10
 */
@ScxMapping("api/crud")
public class CRUDController {

    private final CRUDHandler crudHandler;

    public CRUDController() {
        var crudHandlerClass = ScxContext.findScxModuleInfo(CRUDModule.class).scxModuleExample().crudHandlerClass();
        this.crudHandler = ScxContext.beanFactory().getBean(crudHandlerClass);
    }

    /**
     * 列表查询
     *
     * @param modelName     a {@link java.lang.String} object.
     * @param limit         a {@link java.lang.Integer} object.
     * @param page          a {@link java.lang.Integer} object.
     * @param orderByColumn a {@link java.lang.String} object.
     * @param sortType      a {@link java.lang.String} object.
     * @param whereBodyList a {@link java.util.Map} object.
     * @return a {@link cool.scx.vo.Json} object.
     * @throws cool.scx.exception.HttpRequestException if any.
     * @throws SQLException                            if any.
     */
    @ScxMapping(value = ":modelName/list", method = HttpMethod.POST)
    public Json list(@FromPath String modelName,
                     @FromBody(value = "pagination.limit", required = false) Integer limit,
                     @FromBody(value = "pagination.page", required = false) Integer page,
                     @FromBody(value = "orderBy.orderByColumn", required = false) String orderByColumn,
                     @FromBody(value = "orderBy.sortType", required = false) String sortType,
                     @FromBody(value = "whereBodyList", required = false) List<CRUDWhereBody> whereBodyList
    ) throws HttpRequestException, SQLException {
        var crudListResult = crudHandler.list(modelName, limit, page, orderByColumn, sortType, whereBodyList);
        return Json.ok().put("items", crudListResult.list()).put("total", crudListResult.total());
    }

    /**
     * 获取详细信息
     *
     * @param modelName a {@link java.lang.String} object.
     * @param id        a {@link java.lang.Long} object.
     * @return a {@link cool.scx.vo.Json} object.
     * @throws cool.scx.exception.HttpRequestException if any.
     * @throws SQLException                            if any.
     */
    @ScxMapping(value = ":modelName/:id", method = HttpMethod.GET)
    public Json info(@FromPath String modelName, @FromBody Long id) throws HttpRequestException, SQLException {
        var info = crudHandler.info(modelName, id);
        return Json.ok().put("info", info);
    }

    /**
     * 保存
     *
     * @param modelName a {@link java.lang.String} object.
     * @param entityMap a {@link java.util.Map} object.
     * @return a {@link cool.scx.vo.Json} object.
     * @throws cool.scx.exception.HttpRequestException if any.
     * @throws SQLException                            if any.
     */
    @ScxMapping(value = ":modelName", method = HttpMethod.POST)
    public Json save(@FromPath String modelName, @FromBody(useAllBody = true) Map<String, Object> entityMap) throws HttpRequestException, SQLException {
        var savedModel = crudHandler.save(modelName, entityMap);
        return Json.ok().put("item", savedModel);
    }

    /**
     * 更新
     *
     * @param modelName a {@link java.lang.String} object.
     * @param entityMap a {@link java.util.Map} object.
     * @return a {@link cool.scx.vo.Json} object.
     * @throws HttpRequestException if any.
     * @throws SQLException         if any.
     */
    @ScxMapping(value = ":modelName", method = HttpMethod.PUT)
    public Json update(@FromPath String modelName, @FromBody(useAllBody = true) Map<String, Object> entityMap) throws HttpRequestException, SQLException {
        var updatedModel = crudHandler.update(modelName, entityMap);
        return Json.ok().put("item", updatedModel);
    }

    /**
     * 删除
     *
     * @param modelName a
     * @param id        a
     * @return j
     * @throws HttpRequestException if any.
     * @throws SQLException         if any.
     */
    @ScxMapping(value = ":modelName/:id", method = HttpMethod.DELETE)
    public Json delete(@FromPath String modelName, @FromBody Long id) throws HttpRequestException, SQLException {
        var b = crudHandler.delete(modelName, id);
        return b ? Json.ok() : Json.fail();
    }

    /**
     * 批量删除
     *
     * @param modelName a {@link java.lang.String} object.
     * @param deleteIDs a {@link java.util.Map} object.
     * @return a {@link cool.scx.vo.Json} object.
     * @throws cool.scx.exception.HttpRequestException if any.
     * @throws java.sql.SQLException                   SQLException
     */
    @ScxMapping(value = ":modelName/batch-delete", method = HttpMethod.DELETE)
    public Json batchDelete(@FromPath String modelName, @FromBody long[] deleteIDs) throws HttpRequestException, SQLException {
        var deletedCount = crudHandler.batchDelete(modelName, deleteIDs);
        return Json.ok().put("deletedCount", deletedCount);
    }

    /**
     * 撤销删除
     *
     * @param modelName a {@link java.lang.String} object.
     * @param id        a {@link java.lang.Integer} object.
     * @return a {@link cool.scx.vo.Json} object.
     * @throws cool.scx.exception.HttpRequestException if any.
     * @throws java.sql.SQLException                   SQLException
     */
    @ScxMapping(value = ":modelName/revoke-delete/:id", method = HttpMethod.GET)
    public Json revokeDelete(@FromPath String modelName, @FromBody Long id) throws HttpRequestException, SQLException {
        if (!ScxContext.easyConfig().tombstone()) {
            return Json.fail("not-used-tombstone");
        } else {
            var b = crudHandler.revokeDelete(modelName, id);
            return b ? Json.ok() : Json.fail();
        }
    }

    /**
     * 获取自动完成字段
     *
     * @param modelName a {@link java.lang.String} object.
     * @param fieldName a {@link java.lang.String} object.
     * @return a {@link cool.scx.vo.Json} object.
     * @throws cool.scx.exception.HttpRequestException if any.
     * @throws java.sql.SQLException                   SQLException
     */
    @ScxMapping(value = ":modelName/get-auto-complete/:fieldName", method = HttpMethod.POST)
    public Json getAutoComplete(@FromPath String modelName, @FromBody String fieldName) throws HttpRequestException, SQLException {
        var fieldList = crudHandler.getAutoComplete(modelName, fieldName);
        return Json.ok().put("fields", fieldList);
    }

    /**
     * 校验唯一性
     *
     * @param modelName a {@link java.lang.String} object.
     * @param fieldName a {@link java.util.Map} object.
     * @param value     a {@link java.util.Map} object.
     * @return a {@link cool.scx.vo.Json} object.
     * @throws cool.scx.exception.HttpRequestException if any.
     * @throws java.sql.SQLException                   SQLException
     */
    @ScxMapping(value = ":modelName/check-unique", method = HttpMethod.POST)
    public Json checkUnique(@FromPath String modelName, @FromBody String fieldName, @FromBody Object value, @FromBody(required = false) Long id) throws HttpRequestException, SQLException {
        var b = crudHandler.checkUnique(modelName, fieldName, value, id);
        return Json.ok().put("isUnique", b);
    }

}
