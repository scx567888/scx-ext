package cool.scx.ext.organization.base;

import cool.scx.core.ScxContext;
import cool.scx.core.annotation.FromBody;
import cool.scx.core.annotation.ScxMapping;
import cool.scx.core.enumeration.HttpMethod;
import cool.scx.core.http.exception.UnauthorizedException;
import cool.scx.core.vo.BaseVo;
import cool.scx.core.vo.DataJson;
import cool.scx.core.vo.Json;
import cool.scx.ext.organization.annotation.ApiPerms;
import cool.scx.ext.organization.auth.DeviceType;
import cool.scx.ext.organization.auth.PermsWrapper;
import cool.scx.ext.organization.auth.ScxAuth;
import cool.scx.ext.organization.exception.AuthException;
import cool.scx.sql.base.UpdateFilter;
import io.vertx.ext.web.RoutingContext;

/**
 * 默认认证 api 推荐使用
 * 也可以不用此 api 但需要将 自定义 AuthHandler 的实现中的方法清空
 *
 * @author scx567888
 * @version 0.3.6
 */
public abstract class BaseAuthController<T extends BaseUser> {

    private final BaseUserService<T> userService;

    /**
     * <p>Constructor for ScxAuthController.</p>
     *
     * @param userService a
     */
    public BaseAuthController(BaseUserService<T> userService) {
        this.userService = userService;
    }

    /**
     * <p>login.</p>
     *
     * @param username a {@link java.lang.String} object
     * @param password a {@link java.lang.String} object
     * @param ctx      a {@link io.vertx.ext.web.RoutingContext} object
     * @return a {@link cool.scx.core.vo.BaseVo} object
     */
    @ScxMapping(method = HttpMethod.POST)
    public BaseVo login(@FromBody String username, @FromBody String password, RoutingContext ctx) {
        try {
            var token = ScxAuth.login(username, password, ctx);
            //这里根据登录设备向客户端返回不同的信息
            if (ScxAuth.getDeviceTypeByHeader(ctx) == DeviceType.WEBSITE) {
                return Json.fail("login-successful");
            } else {
                return Json.ok().put("token", token);
            }
        } catch (AuthException e) {
            return e.toBaseVo();
        }
    }

    /**
     * <p>loginByThirdParty.</p>
     *
     * @param uniqueID    a {@link java.lang.String} object
     * @param accessToken a {@link java.lang.String} object
     * @param accountType a {@link java.lang.String} object
     * @param ctx         a {@link io.vertx.ext.web.RoutingContext} object
     * @return a {@link cool.scx.core.vo.BaseVo} object
     */
    @ScxMapping(method = HttpMethod.POST)
    public BaseVo loginByThirdParty(@FromBody String uniqueID, @FromBody String accessToken, @FromBody String accountType, RoutingContext ctx) {
        try {
            var token = ScxAuth.loginByThirdParty(uniqueID, accessToken, accountType, ctx);
            //这里根据登录设备向客户端返回不同的信息
            if (ScxAuth.getDeviceTypeByHeader(ctx) == DeviceType.WEBSITE) {
                return Json.fail("login-successful");
            } else {
                return Json.ok().put("token", token);
            }
        } catch (AuthException e) {
            return e.toBaseVo();
        }
    }

    /**
     * <p>signup.</p>
     *
     * @param user a
     * @return a {@link cool.scx.core.vo.DataJson} object
     */
    @ScxMapping(method = HttpMethod.POST)
    public DataJson signup(@FromBody(useAllBody = true) T user) {
        var newUser = userService.signup(user);
        return DataJson.ok().data(newUser);
    }

    /**
     * <p>signupByThirdParty.</p>
     *
     * @param uniqueID    a {@link java.lang.String} object
     * @param accessToken a {@link java.lang.String} object
     * @param accountType a {@link java.lang.String} object
     * @return a {@link cool.scx.core.vo.DataJson} object
     */
    @ScxMapping(method = HttpMethod.POST)
    public DataJson signupByThirdParty(@FromBody String uniqueID, @FromBody String accessToken, @FromBody String accountType) {
        var newUser = ScxAuth.signupByThirdParty(uniqueID, accessToken, accountType);
        return DataJson.ok().data(newUser);
    }

    /**
     * <p>logout.</p>
     *
     * @param routingContext a {@link io.vertx.ext.web.RoutingContext} object
     * @return a {@link cool.scx.core.vo.Json} object
     */
    @ScxMapping(method = HttpMethod.POST)
    public Json logout(RoutingContext routingContext) {
        ScxAuth.removeAuthUser(routingContext);
        return Json.ok();
    }

    /**
     * <p>info.</p>
     *
     * @param routingContext a {@link io.vertx.ext.web.RoutingContext} object
     * @return a {@link cool.scx.core.vo.BaseVo} object
     * @throws cool.scx.core.http.exception.UnauthorizedException if any.
     */
    @ApiPerms(checkPerms = false)
    @ScxMapping(method = HttpMethod.GET)
    public BaseVo info(RoutingContext routingContext) throws UnauthorizedException {
        var user = ScxAuth.getLoginUser(routingContext);
        //返回登录用户的信息给前台 含用户基本信息还有的所有角色的权限
        return DataJson.ok().data(new UserInfo(user, ScxAuth.getPerms(user)));
    }

    /**
     * <p>changeUserAvatar.</p>
     *
     * @param newAvatar a {@link java.lang.String} object
     * @return a {@link cool.scx.core.vo.DataJson} object
     * @throws cool.scx.core.http.exception.UnauthorizedException if any.
     */
    @SuppressWarnings("unchecked")
    @ApiPerms(checkPerms = false)
    @ScxMapping(method = HttpMethod.POST)
    public DataJson changeUserAvatar(@FromBody String newAvatar) throws UnauthorizedException {
        var loginUser = (T) ScxAuth.getLoginUser();
        loginUser.avatar = newAvatar;
        return DataJson.ok().data(userService.update(loginUser, UpdateFilter.ofIncluded("avatar")));
    }

    /**
     * <p>changeUserUsername.</p>
     *
     * @param newUsername a {@link java.lang.String} object
     * @param password    a {@link java.lang.String} object
     * @return a {@link cool.scx.core.vo.BaseVo} object
     * @throws cool.scx.core.http.exception.UnauthorizedException if any.
     */
    @ApiPerms(checkPerms = false)
    @ScxMapping(method = HttpMethod.POST)
    public BaseVo changeUserUsername(@FromBody String newUsername, @FromBody String password) throws UnauthorizedException {
        try {
            return DataJson.ok().data(userService.changeUsernameBySelf(newUsername, password));
        } catch (AuthException e) {
            return e.toBaseVo();
        }
    }

    /**
     * <p>changeUserPassword.</p>
     *
     * @param newPassword a {@link java.lang.String} object
     * @param oldPassword a {@link java.lang.String} object
     * @return a {@link cool.scx.core.vo.BaseVo} object
     * @throws cool.scx.core.http.exception.UnauthorizedException if any.
     */
    @ApiPerms(checkPerms = false)
    @ScxMapping(method = HttpMethod.POST)
    public BaseVo changeUserPassword(@FromBody String newPassword, @FromBody String oldPassword) throws UnauthorizedException {
        try {
            return DataJson.ok().data(userService.changePasswordBySelf(newPassword, oldPassword));
        } catch (AuthException e) {
            return e.toBaseVo();
        }
    }

    /**
     * <p>ScxUserInfo class.</p>
     *
     * @author scx567888
     * @version 1.11.8
     */
    public static class UserInfo {

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
         * @param permsWrapper a {@link PermsWrapper} object
         */
        public UserInfo(BaseUser user, PermsWrapper permsWrapper) {
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

}
