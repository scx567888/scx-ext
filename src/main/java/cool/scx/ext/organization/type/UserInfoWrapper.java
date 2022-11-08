package cool.scx.ext.organization.type;

import cool.scx.ext.organization.base.BaseUser;

/**
 * <p>ScxUserInfo class.</p>
 *
 * @author scx567888
 * @version 1.11.8
 */
public class UserInfoWrapper {

    /**
     * id
     */
    public final Long id;

    /**
     * 用户名
     */
    public final String username;

    /**
     * 是否为管理员
     */
    public final Boolean isAdmin;

    /**
     * 头像
     */
    public final String avatar;

    /**
     * 密码
     */
    public final String phoneNumber;

    /**
     * 邮箱地址
     */
    public final String emailAddress;

    /**
     * 通用权限
     */
    public final String[] perms;

    /**
     * 页面权限
     */
    public final String[] pagePerms;

    /**
     * 页面元素权限
     */
    public final String[] pageElementPerms;

    /**
     * <p>Constructor for ScxUserInfo.</p>
     *
     * @param user         a {@link cool.scx.ext.organization.base.BaseUser} object
     * @param permsWrapper a {@link cool.scx.ext.organization.type.PermsWrapper} object
     */
    public UserInfoWrapper(BaseUser user, PermsWrapper permsWrapper) {
        this.id = user.id;
        this.username = user.username;
        this.isAdmin = user.isAdmin;
        this.avatar = user.avatar;
        this.phoneNumber = user.phoneNumber;
        this.emailAddress = user.emailAddress;
        this.perms = permsWrapper.perms().toArray(String[]::new);
        this.pagePerms = permsWrapper.pagePerms().toArray(String[]::new);
        this.pageElementPerms = permsWrapper.pageElementPerms().toArray(String[]::new);
    }

}
