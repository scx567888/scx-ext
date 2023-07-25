package cool.scx.ext.crud;

import cool.scx.core.base.BaseModel;
import cool.scx.data.Query;
import cool.scx.data.query.WhereOption;

import java.util.Map;

import static cool.scx.data.Query.query;
import static cool.scx.data.query.Logic.andSet;

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
        var baseModelClass = CRUDHelper.getCRUDApiInfo(modelName).baseModelClass;
        CRUDHelper.checkFieldName(baseModelClass, fieldName);
        var baseModelService = CRUDHelper.getBaseModelService(baseModelClass);
        var query = query().where(andSet().equal(fieldName, value).notEqual("id", id, WhereOption.SKIP_IF_NULL));
        return baseModelService.count(query) == 0;
    }

    /**
     * 批量删除
     *
     * @param modelName model 名称
     * @param deleteIDs 批量删除的 id
     * @return r
     */
    default long batchDelete(String modelName, long[] deleteIDs) {
        var baseModelClass = CRUDHelper.getCRUDApiInfo(modelName).baseModelClass;
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
        var baseModelClass = CRUDHelper.getCRUDApiInfo(modelName).baseModelClass;
        var baseModelService = CRUDHelper.getBaseModelService(baseModelClass);
        return baseModelService.delete(id) == 1;
    }

    /**
     * 更新数据
     *
     * @param modelName       model 名称
     * @param crudUpdateParam 可以转换为 model类的 map (其中需要存在 id)
     * @return c
     */
    default BaseModel update(String modelName, CRUDUpdateParam crudUpdateParam) {
        var baseModelClass = CRUDHelper.getCRUDApiInfo(modelName).baseModelClass;
        var baseModelService = CRUDHelper.getBaseModelService(baseModelClass);
        var realObject = crudUpdateParam.getBaseModel(baseModelClass);
        var updateFilter = crudUpdateParam.getUpdateFilter(baseModelClass, baseModelService._dao()._tableInfo());
        return baseModelService.update(realObject, updateFilter);
    }

    /**
     * <p>save.</p>
     *
     * @param modelName model 名称
     * @param saveModel 可以转换为 model类的 map
     * @return c
     */
    default BaseModel add(String modelName, Map<String, Object> saveModel) {
        var baseModelClass = CRUDHelper.getCRUDApiInfo(modelName).baseModelClass;
        var baseModelService = CRUDHelper.getBaseModelService(baseModelClass);
        var realObject = CRUDHelper.mapToBaseModel(saveModel, baseModelClass);
        return baseModelService.add(realObject);
    }

    /**
     * 获取单条数据信息
     *
     * @param modelName model 名称
     * @param id        查询的 id
     * @return 单条数据信息
     */
    default BaseModel info(String modelName, Long id) {
        var baseModelClass = CRUDHelper.getCRUDApiInfo(modelName).baseModelClass;
        var baseModelService = CRUDHelper.getBaseModelService(baseModelClass);
        return baseModelService.get(id);
    }

    /**
     * 获取列表数据
     *
     * @param modelName     model 名称
     * @param crudListParam list 查询参数
     * @return 列表数据
     */
    default CRUDListResult list(String modelName, CRUDListParam crudListParam) {
        var baseModelClass = CRUDHelper.getCRUDApiInfo(modelName).baseModelClass;
        var baseModelService = CRUDHelper.getBaseModelService(baseModelClass);
        var query = crudListParam.getQueryOrThrow(baseModelClass);
        var selectFilter = crudListParam.getSelectFilterOrThrow(baseModelClass, baseModelService._dao()._tableInfo());
        var list = baseModelService.list(query, selectFilter);
        var total = baseModelService.count(query);
        return new CRUDListResult(list, total);
    }

}
