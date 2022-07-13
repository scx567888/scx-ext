package cool.scx.ext.organization.dept;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cool.scx.core.annotation.Column;
import cool.scx.core.annotation.ScxModel;
import cool.scx.ext.crud.annotation.UseCRUDApi;
import cool.scx.ext.organization.auth.PermsModel;

/**
 * 部门
 *
 * @author scx567888
 * @version 1.11.8
 */
@UseCRUDApi
@ScxModel(tablePrefix = "organization")
public class Dept extends PermsModel {

    /**
     * 部门名称
     */
    public String deptName;

    /**
     * 排序
     */
    @Column(notNull = true, defaultValue = "0", needIndex = true)
    @JsonIgnore
    public Integer deptOrder;

    /**
     * 父id 用作构建树形结构
     */
    public Long parentID;

}
