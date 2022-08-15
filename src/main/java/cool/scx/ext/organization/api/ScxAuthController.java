package cool.scx.ext.organization.api;

import cool.scx.core.annotation.FromBody;
import cool.scx.core.annotation.ScxMapping;
import cool.scx.core.enumeration.HttpMethod;
import cool.scx.core.http.exception.impl.UnauthorizedException;
import cool.scx.core.vo.BaseVo;
import cool.scx.core.vo.DataJson;
import cool.scx.core.vo.Json;
import cool.scx.ext.organization._impl.User;
import cool.scx.ext.organization._impl.UserService;
import cool.scx.ext.organization.annotation.Perms;
import cool.scx.ext.organization.auth.DeviceType;
import cool.scx.ext.organization.auth.ScxAuth;
import cool.scx.ext.organization.exception.AuthException;
import io.vertx.ext.web.RoutingContext;

/**
 * 默认认证 api 推荐使用
 * 也可以不用此 api 但需要将 自定义 AuthHandler 的实现中的方法清空
 *
 * @author scx567888
 * @version 0.3.6
 */
@ScxMapping("/api/auth")
public class ScxAuthController {

    private final UserService userService;

    /**
     * <p>Constructor for ScxAuthController.</p>
     *
     * @param userService a
     */
    public ScxAuthController(UserService userService) {
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
    public DataJson signup(@FromBody(useAllBody = true) User user) {
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
     * @throws cool.scx.core.http.exception.impl.UnauthorizedException if any.
     */
    @Perms(checkPerms = false)
    @ScxMapping(method = HttpMethod.GET)
    public BaseVo info(RoutingContext routingContext) throws UnauthorizedException {
        var user = ScxAuth.getLoginUser(routingContext);
        //返回登录用户的信息给前台 含用户基本信息还有的所有角色的权限
        return DataJson.ok().data(new ScxUserInfo(user, ScxAuth.getPerms(user)));

    }

    /**
     * <p>changeUserAvatar.</p>
     *
     * @param newAvatar a {@link java.lang.String} object
     * @return a {@link cool.scx.core.vo.DataJson} object
     * @throws cool.scx.core.http.exception.impl.UnauthorizedException if any.
     */
    @Perms(checkPerms = false)
    @ScxMapping(method = HttpMethod.POST)
    public DataJson changeUserAvatar(@FromBody String newAvatar) throws UnauthorizedException {
        var loginUser = ScxAuth.getLoginUser();
        var l = new User();
        l.avatar = newAvatar;
        l.id = loginUser.id;
        return DataJson.ok().data(userService.update(l));
    }

    /**
     * <p>changeUserUsername.</p>
     *
     * @param newUsername a {@link java.lang.String} object
     * @param password    a {@link java.lang.String} object
     * @return a {@link cool.scx.core.vo.BaseVo} object
     * @throws cool.scx.core.http.exception.impl.UnauthorizedException if any.
     */
    @Perms(checkPerms = false)
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
     * @throws cool.scx.core.http.exception.impl.UnauthorizedException if any.
     */
    @Perms(checkPerms = false)
    @ScxMapping(method = HttpMethod.POST)
    public BaseVo changeUserPassword(@FromBody String newPassword, @FromBody String oldPassword) throws UnauthorizedException {
        try {
            return DataJson.ok().data(userService.changePasswordBySelf(newPassword, oldPassword));
        } catch (AuthException e) {
            return e.toBaseVo();
        }
    }

}
