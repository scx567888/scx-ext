package cool.scx.test.auth;

import cool.scx.ScxContext;
import cool.scx.annotation.FromBody;
import cool.scx.annotation.ScxMapping;
import cool.scx.bo.Query;
import cool.scx.enumeration.HttpMethod;
import cool.scx.test.auth.exception.LoginException;
import cool.scx.test.auth.exception.UnknownUserException;
import cool.scx.test.auth.exception.WrongPasswordException;
import cool.scx.test.user.User;
import cool.scx.test.user.UserService;
import cool.scx.util.CryptoUtils;
import cool.scx.vo.Json;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 默认认证 api 推荐使用
 * 也可以不用此 api 但需要将 自定义 AuthHandler 的实现中的方法清空
 *
 * @author scx567888
 * @version 0.3.6
 */
@ScxMapping("/api/auth")
public class TestAuthController {

    private final Logger logger = LoggerFactory.getLogger(TestAuthController.class);

    /**
     * 用户
     */
    private final UserService userService;

    /**
     * @param userService u
     */
    public TestAuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 登录方法
     *
     * @param username 用户名
     * @param password 密码
     * @param ctx      ctx
     * @return json
     */
    @ScxMapping(method = HttpMethod.POST)
    public Json login(@FromBody String username, @FromBody String password, RoutingContext ctx) {
        try {
            // 先获取登录的设备类型
            // 尝试根据设备类型获取一个可以用来认证的 token
            var token = TestAuth.getTokenByCookie(ctx);
            // 尝试登录 登录失败会直接走到 catch 中进行处理
            var loginUser = tryLogin(username, password);
            //走到这里表示 即 "成功获取到了 token" 也 登录成功了 我们将这些信息加入到 TestAuth 里的 ALREADY_LOGIN_CLIENTS 列表中
            TestAuth.addLoginItem(token, loginUser);
            //更新用户的最后一次登录的 时间和ip
            return Json.fail("login-successful");
        } catch (LoginException loginException) {
            if (loginException instanceof UnknownUserException) {
                return Json.fail("user-not-found");
            } else if (loginException instanceof WrongPasswordException) {
                return Json.fail("password-error");
            } else {
                logger.error("登录出错 : {}", loginException.getMessage());
                return Json.fail("logon-failure");
            }
        }
    }

    /**
     * 退出登录方法 同时清空 session 里的登录数据
     *
     * @return 是否成功退出
     */
    @ScxMapping(method = HttpMethod.POST)
    public Json logout() {
        var b = TestAuth.removeAuthUser(ScxContext.routingContext());
        return b ? Json.ok() : Json.fail();
    }

    /**
     * 尝试登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录成功的用户
     * @throws LoginException 登录失败的错误
     */
    private User tryLogin(String username, String password) throws LoginException {
        var user = userService.get(new Query().equal("username", username));
        if (user == null) {
            throw new UnknownUserException();
        } else if (!CryptoUtils.checkPassword(password, user.password)) {
            throw new WrongPasswordException();
        }
        return user;
    }

}
