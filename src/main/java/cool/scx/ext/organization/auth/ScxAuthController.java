package cool.scx.ext.organization.auth;

import cool.scx.ScxContext;
import cool.scx.annotation.FromBody;
import cool.scx.annotation.ScxMapping;
import cool.scx.enumeration.HttpMethod;
import cool.scx.ext.organization.annotation.Perms;
import cool.scx.ext.organization.exception.AuthException;
import cool.scx.ext.organization.user.User;
import cool.scx.ext.organization.user.UserService;
import cool.scx.http.exception.impl.UnauthorizedException;
import cool.scx.vo.BaseVo;
import cool.scx.vo.DataJson;
import cool.scx.vo.Json;
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

    public ScxAuthController(UserService userService) {
        this.userService = userService;
    }

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

    @ScxMapping(method = HttpMethod.POST)
    public DataJson signup(@FromBody(useAllBody = true) User user) {
        var newUser = userService.signup(user);
        return DataJson.ok().data(newUser);
    }

    @ScxMapping(method = HttpMethod.POST)
    public DataJson signupByThirdParty(@FromBody String uniqueID, @FromBody String accessToken, @FromBody String accountType) {
        var newUser = ScxAuth.signupByThirdParty(uniqueID, accessToken, accountType);
        return DataJson.ok().data(newUser);
    }

    @ScxMapping(method = HttpMethod.POST)
    public Json logout(RoutingContext routingContext) {
        ScxAuth.removeAuthUser(routingContext);
        return Json.ok();
    }

    @Perms(checkedPerms = false)
    @ScxMapping(method = HttpMethod.GET)
    public BaseVo info(RoutingContext routingContext) throws UnauthorizedException {
        var user = ScxAuth.getLoginUser(routingContext);
        var permsWrapper = ScxAuth.getPerms(user);
        //返回登录用户的信息给前台 含用户基本信息还有的所有角色的权限
        var id = user.id;
        var username = user.username;
        var isAdmin = user.isAdmin;
        var avatar = user.avatar;
        var phoneNumber = user.phoneNumber;
        var emailAddress = user.emailAddress;
        var perms = permsWrapper.perms().toArray(String[]::new);
        var pagePerms = permsWrapper.pagePerms().toArray(String[]::new);
        var pageElementPerms = permsWrapper.pageElementPerms().toArray(String[]::new);
        var tombstone = ScxContext.easyConfig().tombstone();
        var scxUserInfo = new ScxUserInfo(id, username, isAdmin, avatar, phoneNumber, emailAddress, perms, pagePerms, pageElementPerms, tombstone);
        return DataJson.ok().data(scxUserInfo);

    }

    @Perms(checkedPerms = false)
    @ScxMapping(method = HttpMethod.POST)
    public DataJson changeUserAvatar(@FromBody String newAvatar) throws UnauthorizedException {
        var loginUser = ScxAuth.getLoginUser();
        var l = new User();
        l.avatar = newAvatar;
        l.id = loginUser.id;
        return DataJson.ok().data(userService.update(l));
    }

    @Perms(checkedPerms = false)
    @ScxMapping(method = HttpMethod.POST)
    public BaseVo changeUserUsername(@FromBody String newUsername, @FromBody String password) throws UnauthorizedException {
        try {
            return DataJson.ok().data(userService.changeUsernameBySelf(newUsername, password));
        } catch (AuthException e) {
            return e.toBaseVo();
        }
    }

    @Perms(checkedPerms = false)
    @ScxMapping(method = HttpMethod.POST)
    public BaseVo changeUserPassword(@FromBody String newPassword, @FromBody String oldPassword) throws UnauthorizedException {
        try {
            return DataJson.ok().data(userService.changePasswordBySelf(newPassword, oldPassword));
        } catch (AuthException e) {
            return e.toBaseVo();
        }
    }

    private record ScxUserInfo(Long id, String username, Boolean isAdmin, String avatar, String phoneNumber,
                               String emailAddress, String[] perms, String[] pagePerms, String[] pageElementPerms,
                               boolean tombstone) {
    }

}
