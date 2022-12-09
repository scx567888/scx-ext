package cool.scx.test.auth;

import cool.scx.ext.auth.BaseAuthHandler;
import cool.scx.ext.auth.Perms;
import org.springframework.stereotype.Component;

@Component
public class TestAuthHandler extends BaseAuthHandler<TestUser> {

    protected TestAuthHandler(TestUserService userService) {
        super(userService);
    }

    @Override
    public TestUser signup(String username, String password) {
        return null;
    }

    @Override
    public Perms getPerms(TestUser user) {
        return null;
    }

    @Override
    public TestUser signupByThirdParty(String uniqueID, String accessToken, String accountType) {
        return null;
    }

    @Override
    public boolean hasPerm(String permString) {
        return false;
    }

}
