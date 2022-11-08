package cool.scx.ext.organization.base;

import cool.scx.core.base.BaseModelService;
import cool.scx.sql.base.Query;
import cool.scx.sql.base.SelectFilter;

import java.util.List;

/**
 * RoleService
 *
 * @author scx567888
 * @version 1.1.2
 */
public abstract class BaseRoleService<T extends BaseRole> extends BaseModelService<T> {

    private final UserRoleService userRoleService;

    /**
     * <p>Constructor for CoreRoleService.</p>
     *
     * @param userRoleService a {@link cool.scx.ext.organization.base.UserRoleService} object.
     */
    public BaseRoleService(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    /**
     * 根据 用户获取 角色
     *
     * @param user a {@link cool.scx.ext.organization.base.BaseUser} object
     * @return a {@link java.util.List} object
     */
    public List<T> getRoleListByUser(BaseUser user) {
        var roleIDs = userRoleService.buildListSQL(new Query().equal("userID", user.id), SelectFilter.ofIncluded("roleID"));
        return list(new Query().in("id", roleIDs));
    }

}
