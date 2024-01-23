package cool.scx.ext.crud;

import cool.scx.core.base.BaseModel;
import cool.scx.data.FieldFilter;
import cool.scx.data.jdbc.AnnotationConfigTable;
import cool.scx.data.jdbc.FieldFilterHelper;
import cool.scx.ext.crud.exception.EmptyUpdateColumnException;

import java.util.Arrays;
import java.util.Map;

import static cool.scx.data.FieldFilter.ofExcluded;
import static cool.scx.data.FieldFilter.ofIncluded;

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
        return cool.scx.ext.crud.CRUDHelper.mapToBaseModel(this.updateModel, modelClass);
    }

    /**
     * 获取 b
     *
     * @param modelClass      a
     * @param scxDaoTableInfo a
     * @return a
     */
    public FieldFilter getUpdateFilter(Class<? extends BaseModel> modelClass, AnnotationConfigTable scxDaoTableInfo) {
        if (needUpdateFieldNames == null) {
            return ofExcluded();
        }
        var legalFieldName = Arrays.stream(needUpdateFieldNames).map(fieldName -> CRUDHelper.checkFieldName(modelClass, fieldName)).toArray(String[]::new);
        var updateFilter = ofIncluded(legalFieldName).excludeIfFieldValueIsNull(false);
        //防止空列更新
        if (FieldFilterHelper.filter(updateFilter, scxDaoTableInfo).length == 0) {
            throw new EmptyUpdateColumnException();
        }
        return updateFilter;
    }

}
