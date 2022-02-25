package cool.scx.test.auth;

import cool.scx.ScxContext;
import cool.scx.test.user.User;
import cool.scx.test.user.UserService;
import cool.scx.util.RandomUtils;
import cool.scx.util.ansi.Ansi;
import io.vertx.core.Handler;
import io.vertx.core.http.impl.CookieImpl;
import io.vertx.ext.web.RoutingContext;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 提供基本的认证逻辑
 *
 * @author scx567888
 * @version 1.1.4
 */
public final class TestAuth {

    /**
     * 获取 token 的标识字段
     */
    private static final String SCX_AUTH_TOKEN_KEY = "S-Token";

    /**
     * 获取 设备 的标识字段
     */
    private static final String SCX_AUTH_DEVICE_KEY = "S-Device";

    /**
     * 存储所有的登录的客户端
     */
    private static final Map<String, User> ALREADY_LOGIN_CLIENTS = new HashMap<>();

    /**
     * SESSION_CACHE 存储路径 默认为 AppRoot 下的  scx-session.cache 文件
     */
    private static final String SCX_TEST_SESSION_PATH = "AppRoot:scx-test-session.bin";

    /**
     * 用户
     */
    private static UserService userService;

    /**
     * 初始化 auth 模块
     */
    public static void initAuth() {
        //设置请求头
        ScxContext.router().corsHandler()
                .allowedHeader(SCX_AUTH_TOKEN_KEY)
                .allowedHeader(SCX_AUTH_DEVICE_KEY);
        //设置 cookie handler
        ScxContext.router().vertxRouter().route().order(1).handler(new TestAuthCookieHandler());
        // 初始化 service
        userService = ScxContext.getBean(UserService.class);
    }

    /**
     * 获取登录用户
     *
     * @return r
     */
    public static User getLoginUser() {
        return getLoginUser(ScxContext.routingContext());
    }

    /**
     * 从文件中读取 LoginItem
     */
    @SuppressWarnings("unchecked")
    public static void readSessionFromFile() {
        var sessionCache = ScxContext.getFileByAppRoot(SCX_TEST_SESSION_PATH);
        try (var f = new FileInputStream(sessionCache); var o = new ObjectInputStream(f)) {
            var loginItems = (Map<? extends String, ? extends User>) o.readObject();
            ALREADY_LOGIN_CLIENTS.putAll(loginItems);
            Ansi.out().brightGreen("成功从 " + sessionCache.getPath() + " 中恢复 " + loginItems.size() + " 条数据!!!").println();
        } catch (Exception ignored) {

        }
    }

    /**
     * 写入 LoginItem 到文件中
     */
    public static void writeSessionToFile() {
        var sessionCache = ScxContext.getFileByAppRoot(SCX_TEST_SESSION_PATH);
        try (var f = new FileOutputStream(sessionCache); var o = new ObjectOutputStream(f)) {
            // 执行模块的 stop 生命周期
            o.writeObject(ALREADY_LOGIN_CLIENTS);
            Ansi.out().red("保存 Session 到 " + sessionCache.getPath() + " 中!!!").println();
        } catch (IOException ignored) {

        }
    }

    /**
     * 根据唯一标识 获取 用户
     * <p>
     * 这里并没有将用户直接存储到 session 中
     * <p>
     * 而是通过此接口进行查找是为了保证用户信息修改后回显的及时性
     *
     * @param ctx c
     * @return 用户
     */
    static User getLoginUser(RoutingContext ctx) {
        return getLoginUserByToken(getTokenByCookie(ctx));
    }

    /**
     * 添加用户到 登录列表中
     *
     * @param token    token
     * @param authUser 认证成功的用户
     */
    static void addLoginItem(String token, User authUser) {
        ALREADY_LOGIN_CLIENTS.put(token, authUser);
    }

    /**
     * 根据 token 获取用户
     *
     * @param token a {@link String} object.
     * @return a {@link User} object.
     */
    public static User getLoginUserByToken(String token) {
        var sessionItem = ALREADY_LOGIN_CLIENTS.get(token);
        if (sessionItem == null) {
            return null;
        }
        return userService.get(sessionItem.id);
    }

    /**
     * 根据 cookie 获取 token
     *
     * @param routingContext a {@link RoutingContext} object
     * @return a {@link String} object
     */
    static String getTokenByCookie(RoutingContext routingContext) {
        return routingContext.request().getCookie(SCX_AUTH_TOKEN_KEY).getValue();
    }

    /**
     * 移除认证用户
     * <p>
     * 使用默认的 路由上下文
     *
     * @return a boolean.
     */
    static boolean removeAuthUser(RoutingContext ctx) {
        String token = getTokenByCookie(ctx);
        return ALREADY_LOGIN_CLIENTS.remove(token) != null;
    }

    private static final class TestAuthCookieHandler implements Handler<RoutingContext> {

        @Override
        public void handle(RoutingContext ctx) {
            if (ctx.request().getCookie(SCX_AUTH_TOKEN_KEY) == null) {
                var cookie = new CookieImpl(SCX_AUTH_TOKEN_KEY, RandomUtils.getUUID());
                cookie.setMaxAge(60 * 60 * 24 * 7);
                ctx.request().response().addCookie(cookie);
            }
            ctx.next();
        }

    }


}
