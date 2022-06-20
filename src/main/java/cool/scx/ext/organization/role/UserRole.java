package cool.scx.ext.organization.role;

import cool.scx.annotation.Column;
import cool.scx.annotation.ScxModel;
import cool.scx.base.BaseModel;

/**
 * 用户角色关联表
 *
 * @author scx567888
 * @version 0.3.6
 */
@ScxModel(tablePrefix = "organization")
public class UserRole extends BaseModel {

    /**
     * 用户 ID
     */
    @Column(notNull = true)
    public Long userID;

    /**
     * 角色的 id
     */
    @Column(notNull = true)
    public Long roleID;

}
