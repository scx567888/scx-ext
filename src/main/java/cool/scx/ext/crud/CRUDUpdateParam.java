package cool.scx.ext.crud;

import cool.scx.base.BaseModel;
import cool.scx.base.UpdateFilter;
import cool.scx.dao.ScxDaoTableInfo;
import cool.scx.ext.crud.exception.EmptyUpdateColumn;

import java.util.Arrays;
import java.util.Map;

/**
 * 更新实体类的封装
 */
public final class CRUDUpdateParam {

    /**
     * 更新的所有内容 可以转换为对应的 实体类
     */
    public Map<String, Object> updateModel;

    /**
     * 需要更新的字段列
     */
    public String[] needUpdateFieldNames;

    /**
     * a
     * @param modelClass a
     * @param <B> a
     * @return a
     */
    public <B extends BaseModel> B getBaseModel(Class<B> modelClass) {
        return CRUDHelper.mapToBaseModel(this.updateModel, modelClass);
    }

    /**
     * 获取 b
     *
     * @param modelClass      a
     * @param scxDaoTableInfo a
     * @return a
     */
    public UpdateFilter getUpdateFilter(Class<? extends BaseModel> modelClass, ScxDaoTableInfo scxDaoTableInfo) {
        if (needUpdateFieldNames == null) {
            return UpdateFilter.ofExcluded();
        }
        var legalFieldName = Arrays.stream(needUpdateFieldNames).map(fieldName -> CRUDHelper.checkFieldName(modelClass, fieldName)).toArray(String[]::new);
        var updateFilter = UpdateFilter.ofIncluded().addIncluded(legalFieldName);
        //防止空列更新 todo 需要吗?
        if (updateFilter.filter(scxDaoTableInfo.columnInfos()).length == 0) {
            throw new EmptyUpdateColumn();
        }
        return updateFilter;
    }

}
