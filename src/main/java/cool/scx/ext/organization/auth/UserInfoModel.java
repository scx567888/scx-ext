package cool.scx.ext.organization.auth;

import cool.scx.core.annotation.Column;
import cool.scx.core.annotation.NoColumn;
import cool.scx.core.base.BaseModel;
import cool.scx.ext.organization.user.User;

/**
 * 和用户有关联的且需要在 model 中能够填充用户的可以继承此类
 * 若其 service 也继承于 {@link cool.scx.ext.organization.auth.UserInfoModelService} 则可以在 list 中直接获得 user 字段数据填充
 *
 * @author scx567888
 * @version 1.11.8
 */
public abstract class UserInfoModel extends BaseModel {

    /**
     * 用户 ID
     */
    @Column(unique = true)
    public Long userID;

    /**
     * 根据用户 ID 查询的用户 为了方便使用
     */
    @NoColumn
    public User user;

}
