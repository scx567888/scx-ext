package cool.scx.ext.organization_impl;

import cool.scx.ext.organization_impl.impl.AuthHandler;
import cool.scx.ext.organization_impl.impl.User;

public class AuthContext {

    private static AuthHandler authHandler;

    public static User getCurrentUser() {
        return authHandler.getCurrentUser();
    }

    static void setAuthHandler(AuthHandler handler) {
        authHandler = handler;
    }

}
