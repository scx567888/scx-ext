package cool.scx.ext.organization.api;

import cool.scx.core.ScxContext;
import cool.scx.ext.organization.auth.PermsWrapper;
import cool.scx.ext.organization.base.BaseUser;

/**
 * <p>ScxUserInfo class.</p>
 *
 * @author scx567888
 * @version 1.11.8
 */
public class ScxUserInfo {

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
     * 当前是否启用墓碑
     */
    public final boolean tombstone;

    /**
     * <p>Constructor for ScxUserInfo.</p>
     *
     * @param user         a {@link BaseUser} object
     * @param permsWrapper a {@link cool.scx.ext.organization.auth.PermsWrapper} object
     */
    public ScxUserInfo(BaseUser user, PermsWrapper permsWrapper) {
        id = user.id;
        username = user.username;
        isAdmin = user.isAdmin;
        avatar = user.avatar;
        phoneNumber = user.phoneNumber;
        emailAddress = user.emailAddress;
        perms = permsWrapper.perms().toArray(String[]::new);
        pagePerms = permsWrapper.pagePerms().toArray(String[]::new);
        pageElementPerms = permsWrapper.pageElementPerms().toArray(String[]::new);
        tombstone = ScxContext.coreConfig().tombstone();
    }

}