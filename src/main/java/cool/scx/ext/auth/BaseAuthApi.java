package cool.scx.ext.auth;

import cool.scx.core.base.BaseModelService;
import cool.scx.dao.UpdateFilter;
import cool.scx.enumeration.HttpMethod;
import cool.scx.ext.auth.annotation.ApiPerms;
import cool.scx.ext.auth.exception.AuthException;
import cool.scx.mvc.annotation.FromBody;
import cool.scx.mvc.annotation.ScxRoute;
import cool.scx.mvc.exception.UnauthorizedException;
import cool.scx.mvc.vo.BaseVo;
import cool.scx.mvc.vo.DataJson;
import cool.scx.mvc.vo.Json;
import io.vertx.ext.web.RoutingContext;

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

    @ScxRoute(methods = HttpMethod.POST)
    public BaseVo login(@FromBody String username, @FromBody String password, RoutingContext ctx) {
        try {
            var token = authHandler.login(username, password, ctx);
            return Json.ok().put("token", token);
        } catch (AuthException e) {
            return e.toBaseVo();
        }
    }

    @ScxRoute(methods = HttpMethod.POST)
    public BaseVo loginByThirdParty(@FromBody String uniqueID, @FromBody String accessToken, @FromBody String accountType, RoutingContext ctx) {
        try {
            var token = authHandler.loginByThirdParty(uniqueID, accessToken, accountType, ctx);
            return Json.ok().put("token", token);
        } catch (AuthException e) {
            return e.toBaseVo();
        }
    }

    @ScxRoute(methods = HttpMethod.POST)
    public BaseVo signup(@FromBody String username, @FromBody String password) {
        try {
            return DataJson.ok().data(authHandler.signup(username, password));
        } catch (AuthException e) {
            return e.toBaseVo();
        }
    }

    @ScxRoute(methods = HttpMethod.POST)
    public BaseVo signupByThirdParty(@FromBody String uniqueID, @FromBody String accessToken, @FromBody String accountType) {
        try {
            return DataJson.ok().data(authHandler.signupByThirdParty(uniqueID, accessToken, accountType));
        } catch (AuthException e) {
            return e.toBaseVo();
        }
    }

    @ScxRoute(methods = HttpMethod.POST)
    public Json logout(RoutingContext routingContext) {
        authHandler.logout(routingContext);
        return Json.ok();
    }

    @ApiPerms(checkPerms = false)
    @ScxRoute(methods = HttpMethod.GET)
    public BaseVo info(RoutingContext routingContext) throws UnauthorizedException {
        var user = authHandler.getCurrentUser(routingContext);
        //返回登录用户的信息给前台 含用户基本信息还有的所有角色的权限
        return DataJson.ok().data(new UserInfo(user, authHandler.getPerms(user)));
    }

    @ApiPerms(checkPerms = false)
    @ScxRoute(methods = HttpMethod.POST)
    public DataJson changeUserAvatar(@FromBody String newAvatar) throws UnauthorizedException {
        var loginUser = authHandler.getCurrentUser();
        loginUser.avatar = newAvatar;
        return DataJson.ok().data(userService.update(loginUser, UpdateFilter.ofIncluded("avatar")));
    }

    @ApiPerms(checkPerms = false)
    @ScxRoute(methods = HttpMethod.POST)
    public BaseVo changeUsernameBySelf(@FromBody String newUsername, @FromBody String password) throws UnauthorizedException {
        try {
            return DataJson.ok().data(authHandler.changeUsernameBySelf(newUsername, password));
        } catch (AuthException e) {
            return e.toBaseVo();
        }
    }

    @ApiPerms(checkPerms = false)
    @ScxRoute(methods = HttpMethod.POST)
    public BaseVo changePasswordBySelf(@FromBody String newPassword, @FromBody String oldPassword) throws UnauthorizedException {
        try {
            return DataJson.ok().data(authHandler.changePasswordBySelf(newPassword, oldPassword));
        } catch (AuthException e) {
            return e.toBaseVo();
        }
    }

    @ApiPerms
    @ScxRoute(methods = {HttpMethod.PUT})
    public BaseVo changePasswordByAdmin(@FromBody String newPassword, @FromBody Long userID) {
        try {
            return DataJson.ok().data(authHandler.changePasswordByAdmin(newPassword, userID));
        } catch (AuthException e) {
            return e.toBaseVo();
        }
    }

}
