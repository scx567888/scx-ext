package cool.scx.ext.organization.base;

import cool.scx.core.annotation.Column;
import cool.scx.core.annotation.ScxModel;
import cool.scx.core.base.BaseModel;

/**
 * 用户部门关联表
 *
 * @author scx567888
 * @version 0.3.6
 */
@ScxModel(tablePrefix = "organization")
public class UserDept extends BaseModel {

    /**
     * 用户 ID
     */
    @Column(notNull = true)
    public Long userID;

    /**
     * 部门的 id
     */
    @Column(notNull = true)
    public Long deptID;

}
