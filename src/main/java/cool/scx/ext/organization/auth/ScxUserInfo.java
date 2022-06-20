package cool.scx.ext.organization.auth;

import cool.scx.ScxContext;
import cool.scx.ext.organization.user.User;

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