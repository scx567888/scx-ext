package cool.scx.ext.organization.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cool.scx.core.annotation.Column;
import cool.scx.ext.organization.type.PermsModel;

/**
 * 部门
 *
 * @author scx567888
 * @version 1.11.8
 */
public class BaseDept extends PermsModel {

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