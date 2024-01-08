package cool.scx.ext.crud;

import cool.scx.core.base.BaseModel;
import cool.scx.core.base.BaseModelService;
import cool.scx.data.query.WhereOption;
import cool.scx.mvc.annotation.FromBody;
import cool.scx.mvc.annotation.FromPath;
import cool.scx.mvc.annotation.ScxRoute;
import cool.scx.mvc.vo.BaseVo;
import cool.scx.mvc.vo.Result;

import java.util.Map;

import static cool.scx.data.QueryBuilder.andSet;
import static cool.scx.data.QueryBuilder.query;
import static cool.scx.enumeration.HttpMethod.*;

/**
 * 继承方式的 CRUD 的 controller (推荐使用)
 *
 * @author scx567888
 * @version 2.5.2
 */
public class BaseCRUDController<T extends BaseModel> {

    private final BaseModelService<T> service;

    public BaseCRUDController(BaseModelService<T> service) {
        this.service = service;
    }

    @ScxRoute(methods = POST)
    public BaseVo list(CRUDListParam crudListParam) {
        var query = crudListParam.getQueryOrThrow(service.entityClass());
        var selectFilter = crudListParam.getSelectFilterOrThrow(service.entityClass(), service.dao().tableInfo());
        var list = service.find(query, selectFilter);
        var total = service.count(query);
        return Result.ok().put("items", list).put("total", total);
    }

    @ScxRoute(value = ":id", methods = GET)
    public BaseVo info(@FromPath Long id) {
        var info = service.get(id);
        return Result.ok(info);
    }

    @ScxRoute(methods = POST)
    public BaseVo add(@FromBody(useAllBody = true) Map<String, Object> saveModel) {
        var realObject = CRUDHelper.mapToBaseModel(saveModel, service.entityClass());
        var savedModel = service.add(realObject);
        return Result.ok(savedModel);
    }

    @ScxRoute(methods = PUT)
    public BaseVo update(CRUDUpdateParam crudUpdateParam) {
        var realObject = crudUpdateParam.getBaseModel(service.entityClass());
        var updateFilter = crudUpdateParam.getUpdateFilter(service.entityClass(), service.dao().tableInfo());
        var updatedModel = service.update(realObject, updateFilter);
        return Result.ok(updatedModel);
    }

    @ScxRoute(value = ":id", methods = DELETE)
    public BaseVo delete(@FromPath Long id) {
        var b = service.delete(id) == 1;
        return b ? Result.ok() : Result.fail();
    }

    @ScxRoute(methods = DELETE)
    public BaseVo batchDelete(@FromBody long[] deleteIDs) {
        var deletedCount = service.delete(deleteIDs);
        return Result.ok().put("deletedCount", deletedCount);
    }

    @ScxRoute(value = "check-unique/:fieldName", methods = POST)
    public BaseVo checkUnique(@FromPath String fieldName, @FromBody Object value, @FromBody(required = false) Long id) {
        CRUDHelper.checkFieldName(service.entityClass(), fieldName);
        var query = query().where(andSet().eq(fieldName, value).ne("id", id, WhereOption.SKIP_IF_NULL));
        var isUnique = service.count(query) == 0;
        return Result.ok().put("isUnique", isUnique);
    }

}
