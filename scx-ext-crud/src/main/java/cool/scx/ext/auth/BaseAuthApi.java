package cool.scx.ext.auth;

import cool.scx.core.annotation.FromBody;
import cool.scx.core.annotation.ScxMapping;
import cool.scx.core.base.BaseModelService;
import cool.scx.core.enumeration.HttpMethod;
import cool.scx.core.http.exception.UnauthorizedException;
import cool.scx.core.vo.BaseVo;
import cool.scx.core.vo.DataJson;
import cool.scx.core.vo.Json;
import cool.scx.ext.auth.annotation.ApiPerms;
import cool.scx.ext.auth.exception.AuthException;
import cool.scx.sql.base.UpdateFilter;
import io.vertx.ext.web.RoutingContext;

import static cool.scx.ext.auth.AuthHelper.getDeviceTypeByHeader;

/**
 * 默认认证 api 推荐使用
 * 也可以不用此 api 但需要将 自定义 AuthHandler 的实现中的方法清空
 *
 * @author scx567888
 * @version 0.3.6
 */
public abstract class BaseAuthApi<T extends BaseUser> {

    /**
     * a
     */
    protected final BaseAuthHandler<T> authHandler;

    /**
     * a
     */
    protected final BaseModelService<T> userService;

    /**
     * a
     *
     * @param authHandler a
     * @param userService a
     */
    protected BaseAuthApi(BaseAuthHandler<T> authHandler, BaseModelService<T> userService) {
        this.authHandler = authHandler;
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
            var token = authHandler.login(username, password, ctx);
            //这里根据登录设备向客户端返回不同的信息
            if (getDeviceTypeByHeader(ctx) == DeviceType.WEBSITE) {
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
            var token = authHandler.loginByThirdParty(uniqueID, accessToken, accountType, ctx);
            //这里根据登录设备向客户端返回不同的信息
            if (getDeviceTypeByHeader(ctx) == DeviceType.WEBSITE) {
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
     * @param username a {@link java.lang.String} object
     * @param password a {@link java.lang.String} object
     * @return a {@link cool.scx.core.vo.DataJson} object
     */
    @ScxMapping(method = HttpMethod.POST)
    public BaseVo signup(@FromBody String username, @FromBody String password) {
        try {
            return DataJson.ok().data(authHandler.signup(username, password));
        } catch (AuthException e) {
            return e.toBaseVo();
        }
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
    public BaseVo signupByThirdParty(@FromBody String uniqueID, @FromBody String accessToken, @FromBody String accountType) {
        try {
            return DataJson.ok().data(authHandler.signupByThirdParty(uniqueID, accessToken, accountType));
        } catch (AuthException e) {
            return e.toBaseVo();
        }
    }

    /**
     * <p>logout.</p>
     *
     * @param routingContext a {@link io.vertx.ext.web.RoutingContext} object
     * @return a {@link cool.scx.core.vo.Json} object
     */
    @ScxMapping(method = HttpMethod.POST)
    public Json logout(RoutingContext routingContext) {
        authHandler.logout(routingContext);
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
        var user = authHandler.getCurrentUser(routingContext);
        //返回登录用户的信息给前台 含用户基本信息还有的所有角色的权限
        return DataJson.ok().data(new UserInfo(user, authHandler.getPerms(user)));
    }

    /**
     * <p>changeUserAvatar.</p>
     *
     * @param newAvatar a {@link java.lang.String} object
     * @return a {@link cool.scx.core.vo.DataJson} object
     * @throws cool.scx.core.http.exception.UnauthorizedException if any.
     */
    @ApiPerms(checkPerms = false)
    @ScxMapping(method = HttpMethod.POST)
    public DataJson changeUserAvatar(@FromBody String newAvatar) throws UnauthorizedException {
        var loginUser = authHandler.getCurrentUser();
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
    public BaseVo changeUsernameBySelf(@FromBody String newUsername, @FromBody String password) throws UnauthorizedException {
        try {
            return DataJson.ok().data(authHandler.changeUsernameBySelf(newUsername, password));
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
    public BaseVo changePasswordBySelf(@FromBody String newPassword, @FromBody String oldPassword) throws UnauthorizedException {
        try {
            return DataJson.ok().data(authHandler.changePasswordBySelf(newPassword, oldPassword));
        } catch (AuthException e) {
            return e.toBaseVo();
        }
    }

    /**
     * <p>changePasswordByAdminUser.</p>
     *
     * @param newPassword a {@link java.lang.String} object
     * @param userID      a {@link java.lang.Long} object
     * @return a {@link cool.scx.core.vo.BaseVo} object
     */
    @ApiPerms
    @ScxMapping(method = {HttpMethod.PUT})
    public BaseVo changePasswordByAdmin(@FromBody String newPassword, @FromBody Long userID) {
        try {
            return DataJson.ok().data(authHandler.changePasswordByAdmin(newPassword, userID));
        } catch (AuthException e) {
            return e.toBaseVo();
        }
    }

}
