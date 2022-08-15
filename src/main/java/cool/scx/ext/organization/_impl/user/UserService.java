package cool.scx.ext.organization._impl.user;

import cool.scx.core.annotation.ScxService;
import cool.scx.ext.organization._impl.dept.DeptService;
import cool.scx.ext.organization._impl.role.RoleService;
import cool.scx.ext.organization.user.BaseUserService;

@ScxService
public class UserService extends BaseUserService<User> {

    public UserService(DeptService deptService, RoleService roleService) {
        super(deptService, roleService);
    }

}
