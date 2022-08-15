package cool.scx.ext.organization._impl;

import cool.scx.core.annotation.ScxService;
import cool.scx.ext.organization.base.BaseRoleService;
import cool.scx.ext.organization.base.UserRoleService;

@ScxService
public class RoleService extends BaseRoleService<Role> {

    public RoleService(UserRoleService userRoleService) {
        super(userRoleService);
    }

}
