package cool.scx.ext.organization.auth;

import java.lang.reflect.Method;

final class OrganizationAuthPerms {

    /**
     * 当前 的权限字符串 规则是  {类名}:{方法名}
     */
    public final String permStr;

    /**
     * 是否检查登录
     */
    public final boolean checkedLogin;

    /**
     * 是否检查权限
     */
    public final boolean checkedPerms;

    /**
     * <p>Constructor for ScxAuthPerms.</p>
     *
     * @param clazz  c
     * @param method m
     */
    public OrganizationAuthPerms(Class<?> clazz, Method method) {
        this.permStr = clazz.getSimpleName() + ":" + method.getName();
        var scxPerms = method.getAnnotation(OrganizationPerms.class);
        if (scxPerms != null) {
            this.checkedPerms = scxPerms.checkedPerms();
            this.checkedLogin = scxPerms.checkedLogin();
        } else {
            this.checkedPerms = false;
            this.checkedLogin = false;
        }
    }

}
