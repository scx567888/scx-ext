package cool.scx.ext.organization._impl;

import cool.scx.core.annotation.ScxService;
import cool.scx.ext.organization.base.BaseUserService;

@ScxService
public class UserService extends BaseUserService<User> {

    public UserService(DeptService deptService, RoleService roleService) {
        super(deptService, roleService);
    }

}
