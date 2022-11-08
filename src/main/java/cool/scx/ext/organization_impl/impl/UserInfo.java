package cool.scx.ext.organization_impl.impl;

import cool.scx.core.ScxContext;
import cool.scx.ext.organization.base.BaseUser;
import cool.scx.ext.organization.type.PermsWrapper;
import cool.scx.ext.organization.type.UserInfoWrapper;

import java.util.List;

public class UserInfo extends UserInfoWrapper {

    public final List<User.LoginInfo> loginInfoHistory;

    /**
     * 当前是否启用墓碑
     */
    public final boolean tombstone;

    /**
     * <p>Constructor for ScxUserInfo.</p>
     *
     * @param user         a {@link BaseUser} object
     * @param permsWrapper a {@link PermsWrapper} object
     */
    public UserInfo(User user, PermsWrapper permsWrapper) {
        super(user, permsWrapper);
        this.loginInfoHistory = user.loginInfoHistory;
        this.tombstone = ScxContext.coreConfig().tombstone();
    }

}
