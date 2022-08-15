package cool.scx.ext.organization._impl;

import cool.scx.core.annotation.ScxService;
import cool.scx.ext.organization.base.BaseRoleService;
import cool.scx.ext.organization.base.UserRoleService;

/**
 * <p>RoleService class.</p>
 *
 * @author scx567888
 * @version 1.13.5
 */
@ScxService
public final class RoleService extends BaseRoleService<Role> {

    /**
     * <p>Constructor for RoleService.</p>
     *
     * @param userRoleService a {@link cool.scx.ext.organization.base.UserRoleService} object
     */
    public RoleService(UserRoleService userRoleService) {
        super(userRoleService);
    }

}
