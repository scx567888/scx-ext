package cool.scx.ext.organization.auth;

import com.fasterxml.jackson.core.type.TypeReference;
import cool.scx.core.ScxContext;
import cool.scx.ext.core.WSParam;
import cool.scx.ext.core.WSParamHandlerRegister;
import cool.scx.ext.organization.base.*;
import cool.scx.ext.organization.exception.AuthException;
import cool.scx.ext.organization.exception.UnknownDeviceException;
import cool.scx.ext.organization.exception.UnknownLoginHandlerException;
import cool.scx.util.ObjectUtils;
import cool.scx.util.RandomUtils;
import cool.scx.util.StringUtils;
import cool.scx.util.ansi.Ansi;
import io.vertx.ext.web.RoutingContext;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * 提供基本的认证逻辑
 * todo 这里的用户登录信息应该做一个缓存 防止多次从数据库中读取
 *
 * @author scx567888
 * @version 1.1.4
 */
public final class ScxAuth {

    /**
     * 获取 token 的标识字段
     */
    static final String SCX_AUTH_TOKEN_KEY = "S-Token";

    /**
     * 获取 设备 的标识字段
     */
    static final String SCX_AUTH_DEVICE_KEY = "S-Device";

    /**
     * 存储所有的登录的客户端
     */
    private static final AlreadyLoginClientMap ALREADY_LOGIN_CLIENT_MAP = new AlreadyLoginClientMap();

    /**
     * SESSION_CACHE 存储路径 默认为 AppRoot 下的  scx-session.cache 文件
     */
    private static final Path SCX_SESSION_CACHE_PATH = ScxContext.getPathByAppRoot("AppRoot:scx-session.cache");

    /**
     * 第三方登录 login handler 映射
     */
    private static final Map<String, ThirdPartyLoginHandler<?>> THIRD_PARTY_LOGIN_HANDLER_MAP = new HashMap<>();

    /**
     * 用户
     */
    private static BaseUserService<?> userService;

    /**
     * 角色
     */
    private static BaseRoleService<?> roleService;

    /**
     * 部门
     */
    private static BaseDeptService<?> deptService;

    /**
     * 初始化 auth 模块
     */
    public static void initAuth(Class<? extends BaseUserService<?>> userServiceClass, Class<? extends BaseDeptService<?>> deptServiceClass, Class<? extends BaseRoleService<?>> roleServiceClass) {
        //绑定事件
        WSParamHandlerRegister.addHandler("bind-websocket-by-token", ScxAuth::bindWebSocketByToken);
        //设置处理器 ScxMapping 前置处理器
        ScxContext.scxMappingConfiguration().setScxMappingInterceptor(new PermsAnnotationInterceptor());
        //设置请求头
        ScxContext.router().corsHandler().allowedHeader(SCX_AUTH_TOKEN_KEY).allowedHeader(SCX_AUTH_DEVICE_KEY);
        //设置 cookie handler
        ScxContext.router().vertxRouter().route().order(1).handler(new ScxAuthCookieHandler());
        // 初始化 service
        userService = ScxContext.getBean(userServiceClass);
        roleService = ScxContext.getBean(roleServiceClass);
        deptService = ScxContext.getBean(deptServiceClass);
    }

    /**
     * 获取登录用户
     *
     * @return r
     */
    public static BaseUser getLoginUser() {
        return getLoginUser(ScxContext.routingContext());
    }

    /**
     * 简单封装方便使用
     *
     * @return s
     */
    public static PermsWrapper getPerms() {
        return getPerms(getLoginUser());
    }

    /**
     * 从文件中读取 LoginItem
     */
    public static void readSessionFromFile() {
        try (var f = Files.newInputStream(SCX_SESSION_CACHE_PATH)) {
            AlreadyLoginClient[] clients = ObjectUtils.jsonMapper().readValue(f, new TypeReference<>() {
            });
            ALREADY_LOGIN_CLIENT_MAP.put(clients);
            Ansi.out().brightGreen("成功从 " + SCX_SESSION_CACHE_PATH + " 中恢复 " + clients.length + " 条数据!!!").println();
        } catch (Exception ignored) {

        }
    }

    /**
     * 写入 LoginItem 到文件中
     */
    public static void writeSessionToFile() {
        try (var f = Files.newOutputStream(SCX_SESSION_CACHE_PATH)) {
            // 执行模块的 stop 生命周期
            f.write(ObjectUtils.toJson(ALREADY_LOGIN_CLIENT_MAP.getAllAlreadyLoginClients()).getBytes(StandardCharsets.UTF_8));
            Ansi.out().red("保存 Session 到 " + SCX_SESSION_CACHE_PATH + " 中!!!").println();
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
    public static BaseUser getLoginUser(RoutingContext ctx) {
        return getLoginUserByToken(getToken(ctx));
    }

    /**
     * 添加用户到 登录列表中
     *
     * @param token       token
     * @param authUser    认证成功的用户
     * @param loginDevice 登录设备
     */
    static void addLoginItem(String token, BaseUser authUser, DeviceType loginDevice) {
        var client = new AlreadyLoginClient();
        client.token = token;
        client.userID = authUser.id;
        client.loginDevice = loginDevice;
        //踢出新用户
        ALREADY_LOGIN_CLIENT_MAP.put(client);
    }

    /**
     * 根据 token 获取用户
     *
     * @param token a {@link java.lang.String} object.
     * @return a {@link BaseUser} object.
     */
    public static BaseUser getLoginUserByToken(String token) {
        var client = ALREADY_LOGIN_CLIENT_MAP.getByToken(token);
        return client != null ? userService.get(client.userID) : null;
    }

    /**
     * a
     *
     * @return a
     */
    public static AlreadyLoginClient getAlreadyLoginClient() {
        return ALREADY_LOGIN_CLIENT_MAP.getByToken(getToken(ScxContext.routingContext()));
    }

    /**
     * 根据 cookie 获取 token
     *
     * @param routingContext a {@link io.vertx.ext.web.RoutingContext} object
     * @return a {@link java.lang.String} object
     */
    static String getTokenFromCookie(RoutingContext routingContext) {
        var cookie = routingContext.request().getCookie(SCX_AUTH_TOKEN_KEY);
        return cookie != null ? cookie.getValue() : null;
    }

    /**
     * 根据 Header 获取 token
     *
     * @param routingContext a {@link io.vertx.ext.web.RoutingContext} object
     * @return a {@link java.lang.String} object
     */
    static String getTokenFromHeader(RoutingContext routingContext) {
        return routingContext.request().getHeader(SCX_AUTH_TOKEN_KEY);
    }

    /**
     * 获取用户的设备
     *
     * @param routingContext a {@link io.vertx.ext.web.RoutingContext} object
     * @return a
     */
    public static DeviceType getDeviceTypeByHeader(RoutingContext routingContext) {
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
            case WEBSITE -> getTokenFromCookie(ctx);
            case ADMIN, APPLE, ANDROID -> getTokenFromHeader(ctx);
            default -> null;
        };
    }

    /**
     * 移除认证用户
     * <p>
     * 使用默认的 路由上下文
     *
     * @param ctx a {@link io.vertx.ext.web.RoutingContext} object
     */
    public static void removeAuthUser(RoutingContext ctx) {
        ALREADY_LOGIN_CLIENT_MAP.removeByToken(getToken(ctx));
    }

    /**
     * 根据 token 绑定 websocket
     *
     * @param wsParam a {@link java.lang.Object} object
     */
    private static void bindWebSocketByToken(WSParam wsParam) {
        var objectMap = ObjectUtils.convertValue(wsParam.data(), ObjectUtils.MAP_TYPE);
        //获取 token
        var token = ObjectUtils.convertValue(objectMap.get("token"), String.class);
        //获取 binaryHandlerID
        var binaryHandlerID = wsParam.webSocket().binaryHandlerID();
        //判断 token 是否有效
        if (StringUtils.notBlank(token)) {
            //这条 websocket 连接所携带的 token 验证通过
            var alreadyLoginClient = ALREADY_LOGIN_CLIENT_MAP.getByToken(token);
            if (alreadyLoginClient != null) {
                alreadyLoginClient.webSocketBinaryHandlerID = binaryHandlerID;
            }
        }
    }

    /**
     * <p>alreadyLoginClients.</p>
     *
     * @return a {@link java.util.List} object
     */
    public static AlreadyLoginClient[] allAlreadyLoginClients() {
        return ALREADY_LOGIN_CLIENT_MAP.getAllAlreadyLoginClients();
    }

    /**
     * 根据用户获取 权限串
     *
     * @param user 用户 (这里只会使用用户的唯一标识 所以其他的字段可以为空)
     * @return 权限字符串集合
     */
    public static PermsWrapper getPerms(BaseUser user) {
        var permissionModelList = new ArrayList<PermsModel>();
        permissionModelList.addAll(deptService.getDeptListByUser(user));
        permissionModelList.addAll(roleService.getRoleListByUser(user));
        var pageElementPerms = new HashSet<String>();
        var pagePerms = new HashSet<String>();
        var perms = new HashSet<String>();
        var apiPerms = new HashSet<String>();
        for (var p : permissionModelList) {
            if (p.pagePerms != null) {
                pagePerms.addAll(p.pagePerms);
            }
            if (p.pageElementPerms != null) {
                pageElementPerms.addAll(p.pageElementPerms);
            }
            if (p.perms != null) {
                perms.addAll(p.perms);
            }
            if (p.apiPerms != null) {
                apiPerms.addAll(p.apiPerms);
            }
        }
        return new PermsWrapper(perms, pagePerms, pageElementPerms, apiPerms);
    }


    /**
     * 尝试获取一个可以作为认证的 Token 具体获取方式由设备类型决定
     *
     * @param ctx         a {@link io.vertx.ext.web.RoutingContext} object
     * @param loginDevice a {@link cool.scx.ext.organization.auth.DeviceType} object
     * @return a {@link java.lang.String} object
     * @throws cool.scx.ext.organization.exception.AuthException if any.
     */
    private static String tryGetAuthToken(RoutingContext ctx, DeviceType loginDevice) throws AuthException {
        //查看登录的设备以判断如何获取 token
        return switch (loginDevice) {
            case ADMIN, ANDROID, APPLE ->
                //这些设备的 token 是保存在 header 中的 所以我们新生成一个 随机id 并将其返回到前台 , 并由前台通过 js 保存到浏览器中
                    RandomUtils.randomUUID();
            case WEBSITE ->
                //这里就是直接通过网页访问 这种情况是没法获取到自定义 header 的所以我们将 cookie 中随机颁发的 token 当作为唯一标识
                    ScxAuth.getTokenFromCookie(ctx);
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
     */
    public static String login(String username, String password, RoutingContext ctx) {
        // 先获取登录的设备类型
        var loginDevice = ScxAuth.getDeviceTypeByHeader(ctx);
        // 尝试根据设备类型获取一个可以用来认证的 token
        var token = tryGetAuthToken(ctx, loginDevice);
        // 尝试登录 登录失败会直接走到 catch 中进行处理
        var loginUser = userService.tryLogin(username, password);
        //走到这里表示 即 "成功获取到了 token" 也 登录成功了 我们将这些信息加入到 TestAuth 里的 ALREADY_LOGIN_CLIENTS 列表中
        ScxAuth.addLoginItem(token, loginUser, loginDevice);
        //更新用户的最后一次登录的 时间和ip
        userService.updateLastLoginDateAndIP(loginUser, "PASSWORD");
        //这里根据登录设备向客户端返回不同的信息
        return token;
    }

    /**
     * a
     *
     * @param uniqueID    a
     * @param accessToken a
     * @param accountType a
     * @param ctx         a
     * @return a
     */
    public static String loginByThirdParty(String uniqueID, String accessToken, String accountType, RoutingContext ctx) {
        // 先获取登录的设备类型
        var loginDevice = ScxAuth.getDeviceTypeByHeader(ctx);
        // 尝试根据设备类型获取一个可以用来认证的 token
        var token = tryGetAuthToken(ctx, loginDevice);
        // 尝试登录 登录失败会直接走到 catch 中进行处理
        var loginUser = findThirdPartyLoginHandler(accountType).tryLogin(uniqueID, accessToken);
        //走到这里表示 即 "成功获取到了 token" 也 登录成功了 我们将这些信息加入到 TestAuth 里的 ALREADY_LOGIN_CLIENTS 列表中
        ScxAuth.addLoginItem(token, loginUser, loginDevice);
        //更新用户的最后一次登录的 时间和ip
        userService.updateLastLoginDateAndIP(loginUser, accountType);
        //这里根据登录设备向客户端返回不同的信息
        return token;
    }

    /**
     * 根据第三方注册一个新用户
     *
     * @param uniqueID    a
     * @param accessToken a
     * @param accountType a
     * @return a
     */
    @SuppressWarnings("unchecked")
    public static BaseUser signupByThirdParty(String uniqueID, String accessToken, String accountType) {
        var defaultNewUser = new BaseUser();
        //默认用户名
        defaultNewUser.username = "scx_" + RandomUtils.randomString(8, true);
        defaultNewUser.isAdmin = false;
        var handler = (ThirdPartyLoginHandler<BaseUser>) findThirdPartyLoginHandler(accountType);
        return handler.signup(uniqueID, accessToken, defaultNewUser);
    }

    /**
     * 根据名称查找第三方的 loginHandler
     *
     * @param type 类型
     * @return handler
     */
    public static ThirdPartyLoginHandler<?> findThirdPartyLoginHandler(String type) {
        var thirdPartyLoginHandler = THIRD_PARTY_LOGIN_HANDLER_MAP.get(type);
        if (thirdPartyLoginHandler == null) {
            throw new UnknownLoginHandlerException();
        }
        return thirdPartyLoginHandler;
    }

    /**
     * 添加一个 第三方的 loginHandler
     *
     * @param type                   名称
     * @param thirdPartyLoginHandler handler
     */
    public static void addThirdPartyLoginHandler(String type, ThirdPartyLoginHandler<?> thirdPartyLoginHandler) {
        THIRD_PARTY_LOGIN_HANDLER_MAP.put(type, thirdPartyLoginHandler);
    }

    /**
     * <p>alreadyLoginClientMap.</p>
     *
     * @return a {@link cool.scx.ext.organization.auth.AlreadyLoginClientMap} object
     */
    public static AlreadyLoginClientMap alreadyLoginClientMap() {
        return ALREADY_LOGIN_CLIENT_MAP;
    }

}
