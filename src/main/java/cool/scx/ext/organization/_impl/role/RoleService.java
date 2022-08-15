package cool.scx.ext.organization._impl.role;

import cool.scx.core.annotation.ScxService;
import cool.scx.ext.organization.role.BaseRoleService;
import cool.scx.ext.organization.role.UserRoleService;

@ScxService
public class RoleService extends BaseRoleService<Role> {

    public RoleService(UserRoleService userRoleService) {
        super(userRoleService);
    }

}
