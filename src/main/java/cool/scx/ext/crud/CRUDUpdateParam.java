package cool.scx.ext.crud;

import cool.scx.core.base.BaseModel;
import cool.scx.data.ColumnFilter;
import cool.scx.data.ColumnMapping;
import cool.scx.data.jdbc.mapping.Table;
import cool.scx.ext.crud.exception.EmptyUpdateColumnException;

import java.util.Arrays;
import java.util.Map;

/**
 * 更新实体类的封装
 *
 * @author scx567888
 * @version 1.10.8
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
     *
     * @param modelClass a
     * @param <B>        a
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
    public ColumnFilter getUpdateFilter(Class<? extends BaseModel> modelClass, Table<? extends ColumnMapping> scxDaoTableInfo) {
        if (needUpdateFieldNames == null) {
            return ColumnFilter.ofExcluded();
        }
        var legalFieldName = Arrays.stream(needUpdateFieldNames).map(fieldName -> CRUDHelper.checkFieldName(modelClass, fieldName)).toArray(String[]::new);
        var updateFilter = ColumnFilter.ofIncluded(false).addIncluded(legalFieldName);
        //防止空列更新
        if (updateFilter.filter(scxDaoTableInfo).length == 0) {
            throw new EmptyUpdateColumnException();
        }
        return updateFilter;
    }

}
