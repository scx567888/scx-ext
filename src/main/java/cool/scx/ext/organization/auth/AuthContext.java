package cool.scx.ext.organization.auth;

import cool.scx.ext.organization.base.BaseUser;

public class AuthContext {

    private static AuthHandler<?> authHandler;

    @SuppressWarnings("unchecked")
    public static <T extends BaseUser> T getCurrentUser() {
        return (T) authHandler.getCurrentUser();
    }

    public static void _authHandler(AuthHandler<?> handler) {
        authHandler = handler;
    }

}
