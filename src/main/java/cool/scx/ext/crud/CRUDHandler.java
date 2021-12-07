package cool.scx.ext.crud;

import cool.scx.base.BaseModel;
import cool.scx.bo.Query;

import java.util.List;
import java.util.Map;

/**
 * crud 各个逻辑的 handler
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
        var baseModelService = CRUDHelper.getBaseModelService(modelName);
        var query = new Query().equal(fieldName, value);
        if (id != null) {
            query.notEqual("id", id);
        }
        return baseModelService.count(query) == 0;
    }

    /**
     * 获取自动完成框 (获取此列的所有数据)
     *
     * @param modelName model 名称
     * @param fieldName 列名称
     * @return r
     */
    default List<Map<String, Object>> getAutoComplete(String modelName, String fieldName) {
        var baseModelService = CRUDHelper.getBaseModelService(modelName);
        return baseModelService.getFieldList(fieldName);
    }

    /**
     * @param modelName model 名称
     * @param id        model 的 id
     * @return r
     */
    default boolean revokeDelete(String modelName, Long id) {
        var baseModelService = CRUDHelper.getBaseModelService(modelName);
        var revokeDeleteCount = baseModelService.revokeDelete(id);
        return revokeDeleteCount == 1;
    }

    /**
     * 批量删除
     *
     * @param modelName model 名称
     * @param deleteIDs 批量删除的 id
     * @return r
     */
    default long batchDelete(String modelName, long[] deleteIDs) {
        var baseModelService = CRUDHelper.getBaseModelService(modelName);
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
        var baseModelService = CRUDHelper.getBaseModelService(modelName);
        var deletedCount = baseModelService.delete(id);
        return deletedCount == 1;
    }

    /**
     * 更新数据
     *
     * @param modelName model 名称
     * @param entityMap 可以转换为 model类的 map (其中需要存在 id)
     * @return c
     */
    default BaseModel update(String modelName, Map<String, Object> entityMap) {
        var baseModelService = CRUDHelper.getBaseModelService(modelName);
        var realObject = CRUDHelper.mapToBaseModel(entityMap, modelName);
        return baseModelService.update(realObject);
    }

    /**
     * @param modelName model 名称
     * @param entityMap 可以转换为 model类的 map
     * @return c
     */
    default BaseModel save(String modelName, Map<String, Object> entityMap) {
        var baseModelService = CRUDHelper.getBaseModelService(modelName);
        var realObject = CRUDHelper.mapToBaseModel(entityMap, modelName);
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
        var baseModelService = CRUDHelper.getBaseModelService(modelName);
        return baseModelService.get(id);
    }

    /**
     * 获取列表数据
     *
     * @param modelName       model 名称
     * @param limit           分页:每页数据
     * @param page            分页:页码
     * @param orderByBodyList 排序参数 (字段,类型)
     * @param whereBodyList   查询参数
     * @return 列表数据
     */
    default CRUDListResult list(String modelName, Integer limit, Integer page, List<CRUDOrderByBody> orderByBodyList, List<CRUDWhereBody> whereBodyList) {
        var baseModelService = CRUDHelper.getBaseModelService(modelName);
        var query = CRUDHelper.getQuery(CRUDHelper.getBaseModelClassByName(modelName), limit, page, orderByBodyList, whereBodyList);
        var list = baseModelService.list(query);
        var total = baseModelService.count(query);
        return new CRUDListResult(list, total);
    }

}
