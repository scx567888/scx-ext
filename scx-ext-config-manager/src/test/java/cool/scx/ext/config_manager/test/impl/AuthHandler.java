package cool.scx.ext.config_manager.test.impl;

import cool.scx.ext.auth.BaseAuthHandler;
import cool.scx.ext.auth.Perms;
import org.springframework.stereotype.Component;

@Component
public class AuthHandler extends BaseAuthHandler<User> {

    /**
     * <p>Constructor for BaseAuthHandler.</p>
     *
     * @param userService a
     */
    protected AuthHandler(UserService userService) {
        super(userService);
    }

    @Override
    public User signup(String username, String password) {
        return null;
    }

    @Override
    public Perms getPerms(User user) {
        return null;
    }

    @Override
    public User signupByThirdParty(String uniqueID, String accessToken, String accountType) {
        return null;
    }

    @Override
    public boolean hasPerm(String permString) {
        return false;
    }

}
