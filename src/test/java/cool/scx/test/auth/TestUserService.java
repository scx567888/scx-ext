package cool.scx.test.auth;

import cool.scx.core.annotation.ScxService;
import cool.scx.ext.organization.base.BaseUserService;
import cool.scx.ext.organization.base.UserDeptService;
import cool.scx.ext.organization.base.UserRoleService;

@ScxService
public class TestUserService extends BaseUserService<TestUser> {

    /**
     * 构造函数
     *
     * @param userRoleService a
     * @param userDeptService a
     */
    public TestUserService(UserRoleService userRoleService, UserDeptService userDeptService) {
        super(userRoleService, userDeptService);
    }

}
