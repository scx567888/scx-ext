package cool.scx.test.auth;

import cool.scx.core.annotation.ScxService;
import cool.scx.ext.organization._impl.DeptService;
import cool.scx.ext.organization._impl.RoleService;
import cool.scx.ext.organization.base.BaseDeptService;
import cool.scx.ext.organization.base.BaseRoleService;
import cool.scx.ext.organization.base.BaseUserService;

@ScxService
public class TestUserService extends BaseUserService<TestUser> {

    /**
     * <p>Constructor for UserService.</p>
     *
     * @param deptService a {@link BaseDeptService} object
     * @param roleService a {@link BaseRoleService} object
     */
    public TestUserService(DeptService deptService, RoleService roleService) {
        super(deptService, roleService);
    }

}
