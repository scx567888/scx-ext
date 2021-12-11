package cool.scx.ext.organization;

import cool.scx.ScxContext;
import cool.scx.ext.core.WSBody;
import cool.scx.ext.organization.dept.DeptService;
import cool.scx.ext.organization.exception.MaximumAtLoginInSameTimeException;
import cool.scx.ext.organization.role.RoleService;
import cool.scx.ext.organization.user.User;
import cool.scx.ext.organization.user.UserService;
import cool.scx.util.RandomUtils;
import cool.scx.util.StringUtils;
import cool.scx.util.ansi.Ansi;
import cool.scx.web.handler.ScxCookieHandlerConfiguration;
import cool.scx.web.handler.ScxCorsHandlerConfiguration;
import io.vertx.core.Handler;
import io.vertx.core.http.impl.CookieImpl;
import io.vertx.ext.web.RoutingContext;

import java.io.*;
import java.util.*;

/**
 * 提供基本的认证逻辑
 *
 * @author scx567888
 * @version 1.1.4
 */
public final class OrganizationAuth {

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
    private static final List<AlreadyLoginClient> ALREADY_LOGIN_CLIENTS = new ArrayList<>();

    /**
     * SESSION_CACHE 存储路径 默认为 AppRoot 下的  scx-session.cache 文件
     */
    private static final String SCX_ORGANIZATION_SESSION_PATH = "AppRoot:scx-organization-session.bin";

    /**
     * 用户
     */
    private static UserService userService;

    /**
     * 角色
     */
    private static RoleService roleService;

    /**
     * 部门
     */
    private static DeptService deptService;

    /**
     * 当用户的登录数量达到设定的最大值时如何处理新登录的用户
     * 取值 0 ,阻止新用户登录 (默认值)
     * 取值 1 ,踢出原有的老用户
     */
    private static int whenTheNumberOfLoginsInSameTimeReachesTheMaximum = 0;

    /**
     * 初始化 auth 模块
     */
    static void initAuth() {
        initWhenTheNumberOfLoginsInSameTimeReachesTheMaximum();
        //绑定事件
        ScxContext.eventBus().consumer("bind-websocket-by-token", OrganizationAuth::bindWebSocketByToken);
        //设置处理器 ScxMapping 前置处理器
        ScxContext.scxMappingConfiguration().setScxMappingInterceptor(new OrganizationAuthInterceptor());
        //设置请求头
        ScxCorsHandlerConfiguration.allowedHeader(SCX_AUTH_TOKEN_KEY);
        ScxCorsHandlerConfiguration.allowedHeader(SCX_AUTH_DEVICE_KEY);
        //设置 cookie handler
        ScxCookieHandlerConfiguration.setScxCookieHandler(new OrganizationAuthCookieHandler());
        // 初始化 service
        userService = ScxContext.beanFactory().getBean(UserService.class);
        roleService = ScxContext.beanFactory().getBean(RoleService.class);
        deptService = ScxContext.beanFactory().getBean(DeptService.class);
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
     * 简单封装方便使用
     *
     * @return s
     */
    public static Set<String> getPerms() {
        return getPerms(getLoginUser());
    }

    /**
     * 从文件中读取 LoginItem
     */
    static void readSessionFromFile() {
        var sessionCache = ScxContext.getFileByAppRoot(SCX_ORGANIZATION_SESSION_PATH);
        try (var f = new FileInputStream(sessionCache); var o = new ObjectInputStream(f)) {
            var loginItems = (AlreadyLoginClient[]) o.readObject();
            Collections.addAll(ALREADY_LOGIN_CLIENTS, loginItems);
            Ansi.out().brightGreen("成功从 " + sessionCache.getPath() + " 中恢复 " + loginItems.length + " 条数据!!!").println();
        } catch (Exception ignored) {

        }
    }

    /**
     * 写入 LoginItem 到文件中
     */
    static void writeSessionToFile() {
        var sessionCache = ScxContext.getFileByAppRoot(SCX_ORGANIZATION_SESSION_PATH);
        try (var f = new FileOutputStream(sessionCache); var o = new ObjectOutputStream(f)) {
            // 执行模块的 stop 生命周期
            o.writeObject(ALREADY_LOGIN_CLIENTS.toArray());
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
        return getLoginUserByToken(getToken(ctx));
    }

    /**
     * 添加用户到 登录列表中
     *
     * @param token       token
     * @param authUser    认证成功的用户
     * @param loginDevice 登录设备
     */
    static void addLoginItem(String token, User authUser, DeviceType loginDevice) {
        var thisUserLoginItemCount = ALREADY_LOGIN_CLIENTS.stream().filter(u -> authUser.id.equals(u.user().id)).count();
        if (thisUserLoginItemCount >= authUser.maxNumberToLoginInSameTime) {
            //阻止新用户登录
            if (whenTheNumberOfLoginsInSameTimeReachesTheMaximum == 0) {
                throw new MaximumAtLoginInSameTimeException();
            } else if (whenTheNumberOfLoginsInSameTimeReachesTheMaximum == 1) {//踢出老用户
                //寻找到第一个老用户
                var firstOldUser = ALREADY_LOGIN_CLIENTS.stream().filter(u -> authUser.id.equals(u.user().id)).findFirst().orElse(null);
                ALREADY_LOGIN_CLIENTS.remove(firstOldUser);
                //添加新用户
                ALREADY_LOGIN_CLIENTS.add(new AlreadyLoginClient(token, authUser, loginDevice));
            }
        } else {
            //添加新用户
            ALREADY_LOGIN_CLIENTS.add(new AlreadyLoginClient(token, authUser, loginDevice));
        }
    }

    /**
     * 根据 token 获取用户
     *
     * @param token a {@link java.lang.String} object.
     * @return a {@link User} object.
     */
    public static User getLoginUserByToken(String token) {
        var sessionItem = ALREADY_LOGIN_CLIENTS.stream().filter(u -> u.token().equals(token)).findAny().orElse(null);
        if (sessionItem == null) {
            return null;
        }
        return userService.get(sessionItem.user().id);
    }

    /**
     * 根据 cookie 获取 token
     *
     * @param routingContext a {@link io.vertx.ext.web.RoutingContext} object
     * @return a {@link java.lang.String} object
     */
    static String getTokenByCookie(RoutingContext routingContext) {
        return routingContext.request().getCookie(SCX_AUTH_TOKEN_KEY).getValue();
    }

    /**
     * 根据 Header 获取 token
     *
     * @param routingContext a {@link io.vertx.ext.web.RoutingContext} object
     * @return a {@link java.lang.String} object
     */
    static String getTokenByHeader(RoutingContext routingContext) {
        return routingContext.request().getHeader(SCX_AUTH_TOKEN_KEY);
    }

    /**
     * 获取用户的设备
     *
     * @param routingContext a {@link io.vertx.ext.web.RoutingContext} object
     */
    static DeviceType getDeviceTypeByHeader(RoutingContext routingContext) {
        String device = routingContext.request().getHeader(SCX_AUTH_DEVICE_KEY);
        if (device == null) {
            return DeviceType.WEBSITE;
        }
        return DeviceType.of(device);
    }

    /**
     * 根据 设备类型自行判断 获取 token
     *
     * @param ctx a {@link io.vertx.ext.web.RoutingContext} object
     * @return a {@link java.lang.String} object
     */
    private static String getToken(RoutingContext ctx) {
        var device = getDeviceTypeByHeader(ctx);
        return switch (device) {
            case WEBSITE -> getTokenByCookie(ctx);
            case ADMIN, APPLE, ANDROID -> getTokenByHeader(ctx);
            default -> null;
        };
    }

    /**
     * 根据用户获取 权限串
     *
     * @param user 用户 (这里只会使用用户的唯一标识 所以其他的字段可以为空)
     * @return 权限字符串集合
     */
    static HashSet<String> getPerms(User user) {
        var permList = new HashSet<String>();
        //如果是超级管理员或管理员 直接设置为 *
        if (user.isAdmin) {
            permList.add("*");
        } else {
            roleService.getRoleListByUser(user).forEach(role -> permList.addAll(role.perms));
            deptService.getDeptListByUser(user).forEach(dept -> permList.addAll(dept.perms));
        }
        return permList;
    }

    /**
     * 移除认证用户
     * <p>
     * 使用默认的 路由上下文
     *
     * @return a boolean.
     */
    static boolean removeAuthUser(RoutingContext ctx) {
        String token = getToken(ctx);
        return ALREADY_LOGIN_CLIENTS.removeIf(i -> i.token().equals(token));
    }

    /**
     * 根据 token 绑定 websocket
     *
     * @param o a {@link java.lang.Object} object
     */
    private static void bindWebSocketByToken(Object o) {
        var wsBody = (WSBody) o;
        //获取 token
        var token = wsBody.data().get("token").asText();
        //获取 binaryHandlerID
        var binaryHandlerID = wsBody.webSocket().binaryHandlerID();
        //判断 token 是否有效
        if (StringUtils.isNotBlank(token)) {
            //这条 websocket 连接所携带的 token 验证通过
            ALREADY_LOGIN_CLIENTS.stream()
                    .filter(u -> u.token().equals(token))
                    .forEach(c -> c.webSocketBinaryHandlerID(binaryHandlerID));
        }
    }

    /**
     * 初始化 initWhenTheNumberOfLoginsInSameTimeReachesTheMaximum 值
     */
    private static void initWhenTheNumberOfLoginsInSameTimeReachesTheMaximum() {
        var s = ScxContext.config().get("organization.when-the-number-of-logins-in-same-time-reaches-the-maximum", String.class);
        if ("block-new-users-login".equalsIgnoreCase(s)) {
            whenTheNumberOfLoginsInSameTimeReachesTheMaximum = 0;
        } else if ("kick-out-old-users".equalsIgnoreCase(s)) {
            whenTheNumberOfLoginsInSameTimeReachesTheMaximum = 1;
        }
    }

    private static final class OrganizationAuthCookieHandler implements Handler<RoutingContext> {

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
