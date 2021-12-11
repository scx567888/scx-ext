package cool.scx.test.dept;

import cool.scx.annotation.Column;
import cool.scx.annotation.ScxModel;
import cool.scx.base.BaseModel;

/**
 * 用户部门关联表
 *
 * @author scx567888
 * @version 0.3.6
 */
@ScxModel(tablePrefix = "test")
public class UserDept extends BaseModel {

    /**
     * 用户的 id
     */
    @Column(notNull = true)
    public Long userID;

    /**
     * 部门的 id
     */
    @Column(notNull = true)
    public Long deptID;

}
