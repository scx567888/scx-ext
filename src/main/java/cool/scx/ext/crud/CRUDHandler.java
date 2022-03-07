package cool.scx.ext.crud;

import cool.scx.base.BaseModel;
import cool.scx.base.Query;
import cool.scx.sql.where.WhereOption;

import java.util.List;
import java.util.Map;

/**
 * crud 各个逻辑的 handler
 *
 * @author scx567888
 * @version 1.7.7
 */
public interface CRUDHandler {

    /**
     * 检查字段值唯一性
     *
     * @param modelName model 名称
     * @param fieldName 列名称
     * @param value     列值
     * @param id        id  (不为空时会排除此 id 进行唯一性检查)
     * @return 是否唯一
     */
    default boolean checkUnique(String modelName, String fieldName, Object value, Long id) {
        var baseModelClass = CRUDHelper.getBaseModelClass(modelName);
        CRUDHelper.checkFieldName(baseModelClass, fieldName);
        var baseModelService = CRUDHelper.getBaseModelService(baseModelClass);
        var query = new Query().equal(fieldName, value).notEqual("id", id, WhereOption.SKIP_IF_NULL);
        return baseModelService.count(query) == 0;
    }

    /**
     * <p>revokeDelete.</p>
     *
     * @param modelName model 名称
     * @param id        model 的 id
     * @return r
     */
    default boolean revokeDelete(String modelName, Long id) {
        var baseModelClass = CRUDHelper.getBaseModelClass(modelName);
        var baseModelService = CRUDHelper.getBaseModelService(baseModelClass);
        return baseModelService.revokeDelete(id) == 1;
    }

    /**
     * 批量删除
     *
     * @param modelName model 名称
     * @param deleteIDs 批量删除的 id
     * @return r
     */
    default long batchDelete(String modelName, long[] deleteIDs) {
        var baseModelClass = CRUDHelper.getBaseModelClass(modelName);
        var baseModelService = CRUDHelper.getBaseModelService(baseModelClass);
        return baseModelService.delete(deleteIDs);
    }

    /**
     * 删除
     *
     * @param modelName model 名称
     * @param id        model 类的 id
     * @return r
     */
    default boolean delete(String modelName, Long id) {
        var baseModelClass = CRUDHelper.getBaseModelClass(modelName);
        var baseModelService = CRUDHelper.getBaseModelService(baseModelClass);
        return baseModelService.delete(id) == 1;
    }

    /**
     * 更新数据
     *
     * @param modelName model 名称
     * @param entityMap 可以转换为 model类的 map (其中需要存在 id)
     * @return c
     */
    default BaseModel update(String modelName, Map<String, Object> entityMap) {
        var baseModelClass = CRUDHelper.getBaseModelClass(modelName);
        var baseModelService = CRUDHelper.getBaseModelService(baseModelClass);
        var realObject = CRUDHelper.mapToBaseModel(entityMap, baseModelClass);
        return baseModelService.update(realObject);
    }

    /**
     * <p>save.</p>
     *
     * @param modelName model 名称
     * @param entityMap 可以转换为 model类的 map
     * @return c
     */
    default BaseModel save(String modelName, Map<String, Object> entityMap) {
        var baseModelClass = CRUDHelper.getBaseModelClass(modelName);
        var baseModelService = CRUDHelper.getBaseModelService(baseModelClass);
        var realObject = CRUDHelper.mapToBaseModel(entityMap, baseModelClass);
        return baseModelService.save(realObject);
    }

    /**
     * 获取单条数据信息
     *
     * @param modelName model 名称
     * @param id        查询的 id
     * @return 单条数据信息
     */
    default BaseModel info(String modelName, Long id) {
        var baseModelClass = CRUDHelper.getBaseModelClass(modelName);
        var baseModelService = CRUDHelper.getBaseModelService(baseModelClass);
        return baseModelService.get(id);
    }

    /**
     * 获取列表数据
     *
     * @param modelName        model 名称
     * @param limit            分页:每页数据
     * @param page             分页:页码
     * @param orderByBodyList  排序参数 (字段,类型)
     * @param whereBodyList    查询参数
     * @param selectFilterBody 查询列过滤项
     * @return 列表数据
     */
    default CRUDListResult list(String modelName, Integer limit, Integer page, List<CRUDOrderByBody> orderByBodyList, List<CRUDWhereBody> whereBodyList, CRUDSelectFilterBody selectFilterBody) {
        var baseModelClass = CRUDHelper.getBaseModelClass(modelName);
        var baseModelService = CRUDHelper.getBaseModelService(baseModelClass);
        var query = CRUDHelper.getQuery(baseModelClass, limit, page, orderByBodyList, whereBodyList);
        var selectFilter = CRUDHelper.getSelectFilter(baseModelClass, selectFilterBody, baseModelService._scxDaoTableInfo());
        var list = baseModelService.list(query, selectFilter);
        var total = baseModelService.count(query);
        return new CRUDListResult(list, total);
    }

}
