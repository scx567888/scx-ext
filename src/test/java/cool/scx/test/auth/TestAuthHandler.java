package cool.scx.test.auth;

import cool.scx.ext.organization.auth.BaseAuthHandler;
import cool.scx.ext.organization.base.*;
import org.springframework.stereotype.Component;

@Component
public class TestAuthHandler extends BaseAuthHandler<TestUser> {

    protected TestAuthHandler(BaseUserService<TestUser> userService, BaseRoleService<?> roleService, BaseDeptService<?> deptService, UserDeptService userDeptService, UserRoleService userRoleService) {
        super(userService, roleService, deptService, userDeptService, userRoleService);
    }

    @Override
    public TestUser signup(String username, String password) {
        return null;
    }

    @Override
    public TestUser signupByThirdParty(String uniqueID, String accessToken, String accountType) {
        return null;
    }

}
