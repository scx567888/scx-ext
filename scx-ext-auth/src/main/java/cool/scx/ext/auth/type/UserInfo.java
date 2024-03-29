package cool.scx.ext.auth.type;

import cool.scx.ext.auth.BaseUser;

/**
 * todo 如果移除此类 getUserInfo 直接返回 { user : xxx, perms : xxx } 会不会更好 ? 会不会存在暴露 user 内部信息的可能 ?
 *
 * @author scx567888
 * @version 1.11.8
 */
public class UserInfo {

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
     * @param user  a {@link cool.scx.ext.auth.BaseUser} object
     * @param perms a {@link Perms} object
     */
    public UserInfo(BaseUser user, Perms perms) {
        this.id = user.id;
        this.username = user.username;
        this.isAdmin = user.isAdmin;
        this.avatar = user.avatar;
        this.phoneNumber = user.phoneNumber;
        this.emailAddress = user.emailAddress;
        this.perms = perms.perms().toArray(String[]::new);
        this.pagePerms = perms.pagePerms().toArray(String[]::new);
        this.pageElementPerms = perms.pageElementPerms().toArray(String[]::new);
    }

}
