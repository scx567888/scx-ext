package cool.scx.ext.organization.base.impl;

import cool.scx.core.annotation.ScxService;
import cool.scx.ext.organization.base.BaseUserService;

/**
 * <p>UserService class.</p>
 *
 * @author scx567888
 * @version 1.13.5
 */
@ScxService
public final class UserService extends BaseUserService<User> {

    /**
     * <p>Constructor for UserService.</p>
     *
     * @param deptService a {@link cool.scx.ext.organization.base.impl.DeptService} object
     * @param roleService a {@link cool.scx.ext.organization.base.impl.RoleService} object
     */
    public UserService(DeptService deptService, RoleService roleService) {
        super(deptService, roleService);
    }

}
