package cool.scx.ext.organization.auth;

import cool.scx.ScxContext;
import cool.scx.annotation.FromBody;
import cool.scx.annotation.ScxMapping;
import cool.scx.bo.Query;
import cool.scx.enumeration.HttpMethod;
import cool.scx.exception.impl.UnauthorizedException;
import cool.scx.ext.organization.OrganizationConfig;
import cool.scx.ext.organization.User;
import cool.scx.ext.organization.UserService;
import cool.scx.util.CryptoUtils;
import cool.scx.util.NetUtils;
import cool.scx.util.RandomUtils;
import cool.scx.vo.Json;
import io.vertx.ext.web.RoutingContext;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * 默认认证 api 推荐使用
 * 也可以不用此 api 但需要将 自定义 AuthHandler 的实现中的方法清空
 *
 * @author scx567888
 * @version 0.3.6
 */
@ScxMapping("/api/auth")
public class OrganizationAuthController {

    /**
     * 用户
     */
    private final UserService userService;

    /**
     * @param userService u
     */
    public OrganizationAuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 尝试获取一个可以作为认证的 Token 具体获取方式由设备类型决定
     *
     * @param ctx ctx
     * @return token
     * @throws OrganizationLoginException c
     */
    private static String tryGetAuthToken(RoutingContext ctx, OrganizationAuthDeviceType loginDevice) throws OrganizationLoginException {
        //查看登录的设备以判断如何获取 token
        return switch (loginDevice) {
            case ADMIN, ANDROID, APPLE ->
                    //这些设备的 token 是保存在 header 中的 所以我们新生成一个 随机id 并将其返回到前台 , 并由前台通过 js 保存到浏览器中
                    RandomUtils.getUUID();
            case WEBSITE ->
                    //这里就是直接通过网页访问 这种情况是没法获取到自定义 header 的所以我们将 cookie 中随机颁发的 token 当作为唯一标识
                    OrganizationAuth.getTokenByCookie(ctx);
            case UNKNOWN ->
                    //这里就不知道 设备类型了 我们直接抛出一个异常
                    throw new UnknownDeviceException();
        };
    }

    /**
     * 登录方法
     *
     * @param username 用户名
     * @param password 密码
     * @param ctx      ctx
     * @return json
     * @throws java.sql.SQLException SQLException
     */
    @ScxMapping(method = HttpMethod.POST)
    public Json login(@FromBody String username, @FromBody String password, RoutingContext ctx) throws SQLException {
        try {
            // 先获取登录的设备类型
            var loginDevice = OrganizationAuth.getDeviceTypeByHeader(ctx);
            // 尝试根据设备类型获取一个可以用来认证的 token
            var token = tryGetAuthToken(ctx, loginDevice);
            // 尝试登录 登录失败会直接走到 catch 中进行处理
            var loginUser = tryLogin(username, password);
            //走到这里表示 即 "成功获取到了 token" 也 登录成功了 我们将这些信息加入到 OrganizationAuth 里的 LOGIN_ITEMS 列表中
            //如果 LOGIN_ITEMS 已经有当前登录的用户, 并且设备类型也和当前的设备类型相同, 我们则对其 token 进行一个替换
            //也就是说这里的规则为 针对单一用户在同类型设备上只允许同时登录一次
            OrganizationAuth.addLoginItem(token, loginUser, loginDevice);
            //更新用户的最后一次登录的 时间和ip
            updateLastLoginDateAndIP(loginUser.id);
            //这里根据登录设备向客户端返回不同的信息
            if (loginDevice == OrganizationAuthDeviceType.WEBSITE) {
                return Json.fail("login-successful");
            } else {
                return Json.ok().put("token", token);
            }
        } catch (OrganizationLoginException organizationLoginException) {
            if (organizationLoginException instanceof UnknownDeviceException) {
                return Json.fail("未知设备");
            } else if (organizationLoginException instanceof UnknownUserException) {
                return Json.fail(OrganizationConfig.confusionLoginError() ? "usernameOrPasswordError" : "userNotFound");
            } else if (organizationLoginException instanceof WrongPasswordException) {
                return Json.fail(OrganizationConfig.confusionLoginError() ? "usernameOrPasswordError" : "passwordError");
            } else {
                System.err.println("登录出错 : " + organizationLoginException.getMessage());
                return Json.fail("logonFailure");
            }
        }
    }

    /**
     * 注册方法
     *
     * @param username 前台发送的用户名
     * @param password 前台发送的密码
     * @return a {@link cool.scx.vo.Json} object.
     * @throws java.sql.SQLException SQLException
     */
    @ScxMapping(method = HttpMethod.POST)
    public Json signup(String username, String password) throws SQLException {
        //判断用户是否存在
        if (userService.get(new Query().equal("username", username)) != null) {
            return Json.fail("userAlreadyExists");
        }
        var user = new User();
        user.username = username;
        user.password = CryptoUtils.encryptPassword(password);
        user.isAdmin = false;
        return userService.save(user) != null ? Json.ok() : Json.fail("signup-error");
    }

    /**
     * 退出登录方法 同时清空 session 里的登录数据
     *
     * @return 是否成功退出
     */
    @ScxMapping(method = HttpMethod.POST)
    public Json logout() {
        var b = OrganizationAuth.removeAuthUser(ScxContext.routingContext());
        System.err.println("当前总登录用户数量 : " + OrganizationAuth.getAllLoginItem().size() + " 个");
        return b ? Json.ok() : Json.fail();
    }

    /**
     * 拉取当前登录用户的信息 (包括权限)
     *
     * @return Json
     * @throws cool.scx.exception.impl.UnauthorizedException if any.
     */
    @ScxMapping(method = HttpMethod.GET)
    public Json info() throws UnauthorizedException {
        var user = OrganizationAuth.getLoginUser(ScxContext.routingContext());
        //从session取出用户信息
        if (user == null) {
            throw new UnauthorizedException();
        } else {
            //返回登录用户的信息给前台 含用户的所有角色和权限
            return Json.ok()
                    .put("id", user.id)
                    .put("username", user.username)
                    .put("nickname", user.nickname)
                    .put("avatar", user.avatar)
                    .put("perms", OrganizationAuth.getPerms(user))
                    .put("tombstone", ScxContext.easyConfig().tombstone());
        }
    }

    /**
     * 尝试登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录成功的用户
     * @throws OrganizationLoginException 登录失败的错误
     */
    private User tryLogin(String username, String password) throws OrganizationLoginException, SQLException {
        var user = userService.get(new Query().equal("username", username));
        if (user == null) {
            throw new UnknownUserException();
        } else if (!CryptoUtils.checkPassword(password, user.password)) {
            throw new WrongPasswordException();
        }
        return user;
    }

    /**
     * 更新有关用户登录的信息
     *
     * @param userID 用户ID
     */
    private void updateLastLoginDateAndIP(Long userID) throws SQLException {
        var oldUser = userService.get(userID);
        if (oldUser.lastLoginIPList == null) {
            oldUser.lastLoginIPList = new ArrayList<>();
        }
        if (oldUser.lastLoginDateList == null) {
            oldUser.lastLoginDateList = new ArrayList<>();
        }
        oldUser.lastLoginDateList.add(LocalDateTime.now());
        oldUser.lastLoginIPList.add(NetUtils.getIpAddress(ScxContext.routingContext()));
        var tempUser = new User();
        //只取最后 10 次
        tempUser.lastLoginDateList = oldUser.lastLoginDateList.subList(Math.max(oldUser.lastLoginDateList.size() - 10, 0), oldUser.lastLoginDateList.size());
        tempUser.lastLoginIPList = oldUser.lastLoginIPList.subList(Math.max(oldUser.lastLoginIPList.size() - 10, 0), oldUser.lastLoginIPList.size());
        userService.update(tempUser, new Query().equal("id", userID));
    }

    @ScxMapping(method = HttpMethod.POST)
    public Json changeUserAvatar(@FromBody String newAvatar) throws UnauthorizedException, SQLException {
        var loginUser = OrganizationAuth.getLoginUser();
        if (loginUser != null) {
            var l = new User();
            l.avatar = newAvatar;
            l.id = loginUser.id;
            var update = userService.update(l);
            return Json.ok().put("item", update);
        } else {
            throw new UnauthorizedException();
        }
    }

    @ScxMapping(method = HttpMethod.POST)
    public Json changeUserUsername(@FromBody String newUsername, @FromBody String password) throws UnauthorizedException, SQLException {
        var loginUser = OrganizationAuth.getLoginUser();
        if (loginUser != null) {
            //密码正确
            if (CryptoUtils.checkPassword(password, loginUser.password)) {
                //判断用户名是否已经被其他人使用
                long count = userService.count(new Query().equal("username", newUsername).notEqual("id", loginUser.id));
                //说明此用户名未被别人使用过
                if (count == 0) {
                    var l = new User();
                    l.username = newUsername;
                    l.id = loginUser.id;
                    var update = userService.update(l);
                    return Json.ok().put("item", update);
                } else {
                    return Json.fail("username-already-exists");
                }
            } else {
                return Json.fail("password-wrong");
            }
        } else {
            throw new UnauthorizedException();
        }
    }

    @ScxMapping(method = HttpMethod.POST)
    public Json changeUserPassword(@FromBody String newPassword, @FromBody String oldPassword) throws UnauthorizedException, SQLException {
        var loginUser = OrganizationAuth.getLoginUser();
        if (loginUser != null) {
            //密码正确
            if (CryptoUtils.checkPassword(oldPassword, loginUser.password)) {
                var l = new User();
                l.password = CryptoUtils.encryptPassword(newPassword);
                l.id = loginUser.id;
                var update = userService.update(l);
                return Json.ok().put("item", update);
            } else {
                return Json.fail("password-wrong");
            }
        } else {
            throw new UnauthorizedException();
        }
    }

}
