package cool.scx.ext.organization.auth;

import cool.scx.ScxContext;
import cool.scx.ext.organization.user.User;

/**
 * <p>ScxUserInfo class.</p>
 *
 * @author scx567888
 * @version 1.11.8
 */
public class ScxUserInfo {
    public final Long id;
    public final String username;
    public final Boolean isAdmin;
    public final String avatar;
    public final String phoneNumber;
    public final String emailAddress;
    public final String[] perms;
    public final String[] pagePerms;
    public final String[] pageElementPerms;
    public final boolean tombstone;

    /**
     * <p>Constructor for ScxUserInfo.</p>
     *
     * @param user         a {@link cool.scx.ext.organization.user.User} object
     * @param permsWrapper a {@link cool.scx.ext.organization.auth.PermsWrapper} object
     */
    public ScxUserInfo(User user, PermsWrapper permsWrapper) {
        id = user.id;
        username = user.username;
        isAdmin = user.isAdmin;
        avatar = user.avatar;
        phoneNumber = user.phoneNumber;
        emailAddress = user.emailAddress;
        perms = permsWrapper.perms().toArray(String[]::new);
        pagePerms = permsWrapper.pagePerms().toArray(String[]::new);
        pageElementPerms = permsWrapper.pageElementPerms().toArray(String[]::new);
        tombstone = ScxContext.easyConfig().tombstone();
    }

}
