package cool.scx.ext.organization_impl;

import cool.scx.ext.organization.auth.PermFlag;
import cool.scx.ext.organization_impl.impl.AuthHandler;
import cool.scx.ext.organization_impl.impl.User;

public class AuthContext {

    private static AuthHandler authHandler;

    public static User getCurrentUser() {
        return authHandler.getCurrentUser();
    }

    public static boolean hasPerm(String permString) {
        return authHandler.hasPerm(permString);
    }

    public static boolean hasPerm(PermFlag permFlag) {
        return authHandler.hasPerm(permFlag);
    }

    static void setAuthHandler(AuthHandler handler) {
        authHandler = handler;
    }

}
