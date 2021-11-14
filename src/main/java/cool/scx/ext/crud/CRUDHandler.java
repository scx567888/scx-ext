package cool.scx.ext.crud;

import cool.scx.base.BaseModel;
import cool.scx.bo.Query;
import cool.scx.exception.HttpRequestException;

import java.sql.SQLException;
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
     * @throws HttpRequestException e
     * @throws SQLException         e
     */
    default boolean checkUnique(String modelName, String fieldName, Object value, Long id) throws HttpRequestException, SQLException {
        var baseService = CRUDHelper.getBaseService(modelName);
        var query = new Query().equal(fieldName, value);
        if (id != null) {
            query.notEqual("id", id);
        }
        return baseService.count(query) == 0;
    }

    /**
     * 获取自动完成框 (获取此列的所有数据)
     *
     * @param modelName model 名称
     * @param fieldName 列名称
     * @return r
     * @throws HttpRequestException e
     * @throws SQLException         e
     */
    default List<Map<String, Object>> getAutoComplete(String modelName, String fieldName) throws HttpRequestException, SQLException {
        var baseService = CRUDHelper.getBaseService(modelName);
        return baseService.getFieldList(fieldName);
    }

    /**
     * @param modelName model 名称
     * @param id        model 的 id
     * @return r
     * @throws HttpRequestException r
     * @throws SQLException         r
     */
    default boolean revokeDelete(String modelName, Long id) throws HttpRequestException, SQLException {
        var baseService = CRUDHelper.getBaseService(modelName);
        var revokeDeleteCount = baseService.revokeDelete(id);
        return revokeDeleteCount == 1;
    }

    /**
     * 批量删除
     *
     * @param modelName model 名称
     * @param deleteIDs 批量删除的 id
     * @return r
     * @throws HttpRequestException e
     * @throws SQLException         e
     */
    default long batchDelete(String modelName, long[] deleteIDs) throws HttpRequestException, SQLException {
        var baseService = CRUDHelper.getBaseService(modelName);
        return baseService.delete(deleteIDs);
    }

    /**
     * 删除
     *
     * @param modelName model 名称
     * @param id        model 类的 id
     * @return r
     * @throws HttpRequestException e
     * @throws SQLException         e
     */
    default boolean delete(String modelName, Long id) throws HttpRequestException, SQLException {
        var baseService = CRUDHelper.getBaseService(modelName);
        var deletedCount = baseService.delete(id);
        return deletedCount == 1;
    }

    /**
     * 更新数据
     *
     * @param modelName model 名称
     * @param entityMap 可以转换为 model类的 map (其中需要存在 id)
     * @return c
     * @throws HttpRequestException e
     * @throws SQLException         e
     */
    default BaseModel update(String modelName, Map<String, Object> entityMap) throws HttpRequestException, SQLException {
        var baseService = CRUDHelper.getBaseService(modelName);
        var realObject = CRUDHelper.mapToBaseModel(entityMap, modelName);
        return baseService.update(realObject);
    }

    /**
     * @param modelName model 名称
     * @param entityMap 可以转换为 model类的 map
     * @return c
     * @throws HttpRequestException e
     * @throws SQLException         e
     */
    default BaseModel save(String modelName, Map<String, Object> entityMap) throws HttpRequestException, SQLException {
        var baseService = CRUDHelper.getBaseService(modelName);
        var realObject = CRUDHelper.mapToBaseModel(entityMap, modelName);
        return baseService.save(realObject);
    }

    /**
     * 获取单条数据信息
     *
     * @param modelName model 名称
     * @param id        查询的 id
     * @return 单条数据信息
     * @throws HttpRequestException e
     * @throws SQLException         e
     */
    default BaseModel info(String modelName, Long id) throws HttpRequestException, SQLException {
        var baseService = CRUDHelper.getBaseService(modelName);
        return baseService.get(id);
    }

    /**
     * 获取列表数据
     *
     * @param modelName     model 名称
     * @param limit         分页:每页数据
     * @param page          分页:页码
     * @param orderByColumn 排序字段
     * @param sortType      排序类型
     * @param whereBodyList 查询参数
     * @return 列表数据
     * @throws HttpRequestException e
     * @throws SQLException         e
     */
    default CRUDListResult list(String modelName, Integer limit, Integer page, String orderByColumn, String sortType, List<CRUDWhereBody> whereBodyList) throws HttpRequestException, SQLException {
        var baseService = CRUDHelper.getBaseService(modelName);
        var query = CRUDHelper.getQuery(CRUDHelper.getBaseModelClassByName(modelName), limit, page, orderByColumn, sortType, whereBodyList);
        var list = baseService.list(query);
        var total = baseService.count(query);
        return new CRUDListResult(list, total);
    }

}
