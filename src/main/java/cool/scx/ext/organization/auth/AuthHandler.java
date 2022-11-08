package cool.scx.ext.organization.auth;

import cool.scx.core.ScxContext;
import cool.scx.ext.organization.base.BaseUser;
import cool.scx.ext.organization.type.PermsWrapper;
import io.vertx.ext.web.RoutingContext;

import static cool.scx.ext.organization.auth.AuthHelper.getToken;

public interface AuthHandler<U extends BaseUser> {

    /**
     * 获取 token 的标识字段
     */
    String SCX_AUTH_TOKEN_KEY = "S-Token";

    /**
     * 获取 设备 的标识字段
     */
    String SCX_AUTH_DEVICE_KEY = "S-Device";


    default U getCurrentUser() {
        return getCurrentUser(ScxContext.routingContext());
    }

    default U getCurrentUser(RoutingContext ctx) {
        return getCurrentUserByToken(getToken(ctx));
    }

    U getCurrentUserByToken(String token);

    String login(String username, String password, RoutingContext context);

    U signup(String username, String password);

    boolean logout(RoutingContext context);

    /**
     * 通过管理员修改用户密码 (不需要原密码)
     *
     * @param newPassword 新密码
     * @param id          id
     * @return user
     */
    U changePasswordByAdmin(String newPassword, Long id);

    /**
     * 修改当前登录用户的 密码
     *
     * @param newPassword 新密码
     * @param oldPassword 用来校验的密码
     * @return a
     */
    U changePasswordBySelf(String newPassword, String oldPassword);

    /**
     * 修改当前登录用户的 用户名
     *
     * @param newUsername 新用户名
     * @param password    用来校验的密码
     * @return a
     */
    U changeUsernameBySelf(String newUsername, String password);

    default PermsWrapper getPerms() {
        return getPerms(getCurrentUser());
    }

    PermsWrapper getPerms(BaseUser user);

    U signupByThirdParty(String uniqueID, String accessToken, String accountType);

    String loginByThirdParty(String uniqueID, String accessToken, String accountType, RoutingContext ctx);

}
