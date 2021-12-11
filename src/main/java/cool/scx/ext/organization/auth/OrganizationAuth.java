package cool.scx.ext.organization.auth;

import cool.scx.ScxContext;
import cool.scx.ScxHandler;
import cool.scx.exception.impl.NoPermException;
import cool.scx.exception.impl.UnauthorizedException;
import cool.scx.ext.organization.dept.DeptService;
import cool.scx.ext.organization.role.RoleService;
import cool.scx.ext.organization.user.User;
import cool.scx.ext.organization.user.UserService;
import cool.scx.mvc.ScxMappingHandler;
import cool.scx.mvc.interceptor.ScxMappingInterceptor;
import cool.scx.util.RandomUtils;
import cool.scx.util.ansi.Ansi;
import cool.scx.web.handler.ScxCookieHandlerConfiguration;
import cool.scx.web.handler.ScxCorsHandlerConfiguration;
import io.vertx.core.Handler;
import io.vertx.core.http.impl.CookieImpl;
import io.vertx.ext.web.RoutingContext;

import java.io.*;
import java.lang.reflect.Method;
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
     * 存储所有的登录用户
     */
    private static final List<LoginItem> LOGIN_ITEMS = new ArrayList<>();

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
     * 初始化 auth 模块
     */
    static void initAuth() {
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
            var loginItems = (LoginItem[]) o.readObject();
            Collections.addAll(LOGIN_ITEMS, loginItems);
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
            o.writeObject(LOGIN_ITEMS.toArray(new LoginItem[0]));
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
     * @param token       a {@link io.vertx.ext.web.RoutingContext} object
     * @param authUser    a {@link User} object
     * @param loginDevice a {@link User} object
     */
    static void addLoginItem(String token, User authUser, OrganizationAuthDeviceType loginDevice) {
        var sessionItem = LOGIN_ITEMS.stream().filter(u -> authUser.id.equals(u.user.id) && loginDevice == u.loginDevice).findAny().orElse(null);
        if (sessionItem == null) {
            LOGIN_ITEMS.add(new OrganizationAuth.LoginItem(token, authUser, loginDevice));
        } else {
            sessionItem.token = token;
        }
    }

    /**
     * 根据 token 获取用户
     *
     * @param token a {@link java.lang.String} object.
     * @return a {@link User} object.
     */
    public static User getLoginUserByToken(String token) {
        var sessionItem = LOGIN_ITEMS.stream().filter(u -> u.token.equals(token)).findAny().orElse(null);
        if (sessionItem == null) {
            return null;
        }
        return userService.get(sessionItem.user.id);
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
     * @return a {@link OrganizationAuthDeviceType} object
     */
    static OrganizationAuthDeviceType getDeviceTypeByHeader(RoutingContext routingContext) {
        String device = routingContext.request().getHeader(SCX_AUTH_DEVICE_KEY);
        if (device == null) {
            return OrganizationAuthDeviceType.WEBSITE;
        }
        return OrganizationAuthDeviceType.of(device);
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
        return LOGIN_ITEMS.removeIf(i -> i.token.equals(token));
    }

    static List<LoginItem> getAllLoginItem() {
        return LOGIN_ITEMS;
    }


    /**
     * 已登录用户对象
     *
     * @author scx567888
     * @version 1.0.10
     */
    private static final class LoginItem implements Serializable {

        /**
         * 唯一 ID 用于标识用户
         */
        public final User user;

        /**
         * 登陆的设备类型
         */
        public final OrganizationAuthDeviceType loginDevice;

        /**
         * 本质上一个是一个随机字符串
         * <p>
         * 前端 通过此值获取登录用户
         * <p>
         * 来源可以多种 header , cookie , url 等
         */
        public String token;

        /**
         * 构造函数
         *
         * @param loginDevice {@link #loginDevice}
         * @param token       {@link #token}
         * @param user        {@link #user}
         */
        public LoginItem(String token, User user, OrganizationAuthDeviceType loginDevice) {
            this.token = token;
            this.user = user;
            this.loginDevice = loginDevice;
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

    private static final class OrganizationAuthInterceptor implements ScxMappingInterceptor {

        /**
         * 缓存池
         */
        private final Map<ScxMappingHandler, OrganizationAuthPerms> SCX_AUTH_PERMS_CACHE = new HashMap<>();

        /**
         * {@inheritDoc}
         */
        @Override
        public void preHandle(RoutingContext context, ScxMappingHandler scxMappingHandler) {
            var p = getScxAuthPerms(scxMappingHandler);
            if (p.checkedLogin) {
                //先获取登录的用户
                var currentUser = OrganizationAuth.getLoginUser(context);
                //如果用户为空 则执行未登录处理器
                if (currentUser == null) {
                    throw new UnauthorizedException();
                } else if (p.checkedPerms && !currentUser.isAdmin && !OrganizationAuth.getPerms(currentUser).contains(p.permStr)) {
                    //否则先查看是否需要校验权限 然后查看是否不为 admin 再查看是否权限串中不包含当前权限 都满足则表示需要执行没权限的 handler
                    throw new NoPermException();
                }
            }
        }

        /**
         * 根据 ScxMappingHandler 获取  ScxAuthPerms (内部使用了简单的缓存)
         *
         * @param s s
         * @return s
         */
        private OrganizationAuthPerms getScxAuthPerms(ScxMappingHandler s) {
            var p = SCX_AUTH_PERMS_CACHE.get(s);
            if (p == null) {
                var scxMappingHandlerPerms = new OrganizationAuthPerms(s.clazz, s.method);
                SCX_AUTH_PERMS_CACHE.put(s, scxMappingHandlerPerms);
                return scxMappingHandlerPerms;
            }
            return p;
        }

    }


    private static final class OrganizationAuthPerms {

        /**
         * 当前 的权限字符串 规则是  {类名}:{方法名}
         */
        public final String permStr;

        /**
         * 是否检查登录
         */
        public final boolean checkedLogin;

        /**
         * 是否检查权限
         */
        public final boolean checkedPerms;

        /**
         * <p>Constructor for ScxAuthPerms.</p>
         *
         * @param clazz  c
         * @param method m
         */
        public OrganizationAuthPerms(Class<?> clazz, Method method) {
            this.permStr = clazz.getSimpleName() + ":" + method.getName();
            var scxPerms = method.getAnnotation(OrganizationPerms.class);
            if (scxPerms != null) {
                this.checkedPerms = scxPerms.checkedPerms();
                this.checkedLogin = scxPerms.checkedLogin();
            } else {
                this.checkedPerms = false;
                this.checkedLogin = false;
            }
        }

    }

}
