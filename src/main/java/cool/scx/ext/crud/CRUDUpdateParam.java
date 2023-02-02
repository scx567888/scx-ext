package cool.scx.ext.crud;

import cool.scx.core.base.BaseModel;
import cool.scx.dao.UpdateFilter;
import cool.scx.ext.crud.exception.EmptyUpdateColumnException;
import cool.scx.sql.TableInfo;

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
    public UpdateFilter getUpdateFilter(Class<? extends BaseModel> modelClass, TableInfo<?> scxDaoTableInfo) {
        if (needUpdateFieldNames == null) {
            return UpdateFilter.ofExcluded();
        }
        var legalFieldName = Arrays.stream(needUpdateFieldNames).map(fieldName -> CRUDHelper.checkFieldName(modelClass, fieldName)).toArray(String[]::new);
        var updateFilter = UpdateFilter.ofIncluded(false).addIncluded(legalFieldName);
        //防止空列更新
        if (updateFilter.filter(scxDaoTableInfo.columnInfos()).length == 0) {
            throw new EmptyUpdateColumnException();
        }
        return updateFilter;
    }

}
