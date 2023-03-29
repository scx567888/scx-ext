package cool.scx.ext.auth;

import com.fasterxml.jackson.databind.JsonNode;
import cool.scx.core.ScxContext;
import cool.scx.core.base.BaseModelService;
import cool.scx.dao.Query;
import cool.scx.dao.query.WhereOption;
import cool.scx.ext.auth.exception.UnknownLoginHandlerException;
import cool.scx.ext.auth.exception.UnknownUserException;
import cool.scx.ext.auth.exception.UsernameAlreadyExistsException;
import cool.scx.ext.auth.exception.WrongPasswordException;
import cool.scx.ext.auth.type.DeviceType;
import cool.scx.ext.auth.type.Perms;
import cool.scx.ext.auth.type.Session;
import cool.scx.ext.auth.type.SessionStore;
import cool.scx.ext.ws.WSMessage;
import cool.scx.mvc.ScxMvc;
import cool.scx.mvc.exception.ForbiddenException;
import cool.scx.mvc.exception.UnauthorizedException;
import cool.scx.util.CryptoUtils;
import cool.scx.util.ObjectUtils;
import cool.scx.util.StringUtils;
import cool.scx.util.ansi.Ansi;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.ext.web.RoutingContext;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static cool.scx.dao.ColumnFilter.ofIncluded;
import static cool.scx.ext.auth.AuthHelper.*;


/**
 * <p>Abstract BaseAuthHandler class.</p>
 *
 * @author scx567888
 * @version 1.15.4
 */
public abstract class BaseAuthHandler<U extends BaseUser> {

    /**
     * SESSION_CACHE 存储路径 默认为 AppRoot 下的  scx-session.cache 文件
     */
    public static final Path SCX_SESSION_CACHE_PATH = ScxContext.getPathByAppRoot("AppRoot:scx-session.cache");

    /**
     * 获取 token 的标识字段
     */
    public static final String SCX_AUTH_TOKEN_KEY = "S-Token";

    /**
     * 获取 设备 的标识字段
     */
    public static final String SCX_AUTH_DEVICE_KEY = "S-Device";

    /**
     * 存储所有的登录的客户端
     */
    protected final SessionStore SESSION_STORE = new SessionStore();

    /**
     * 第三方登录 login handler 映射
     */
    protected final Map<String, ThirdPartyLoginHandler<?>> THIRD_PARTY_LOGIN_HANDLER_MAP = new HashMap<>();

    /**
     * 用户
     */
    protected final BaseModelService<U> userService;

    /**
     * <p>Constructor for BaseAuthHandler.</p>
     *
     * @param userService a
     */
    protected BaseAuthHandler(BaseModelService<U> userService) {
        this.userService = userService;
    }

    /**
     * a
     *
     * @return a
     */
    public U getCurrentUser() {
        return getCurrentUser(ScxMvc.routingContext());
    }

    /**
     * a
     *
     * @param ctx a
     * @return a
     */
    public U getCurrentUser(RoutingContext ctx) {
        return getCurrentUserByToken(getToken(ctx));
    }

    /**
     * a
     *
     * @param token a
     * @return a
     */
    public U getCurrentUserByToken(String token) {
        var client = SESSION_STORE.getByToken(token);
        return client != null ? userService.get(client.userID()) : null;
    }

    /**
     * a
     *
     * @param username a
     * @param password a
     * @param ctx      a
     * @return a
     */
    public Session login(String username, String password, RoutingContext ctx) throws UnknownUserException, WrongPasswordException {
        // 先获取登录的设备类型
        var loginDevice = getDeviceTypeByHeader(ctx);
        // 尝试根据设备类型获取一个可以用来认证的 token
        var token = tryGetAuthToken(ctx, loginDevice);
        // 尝试登录 登录失败会直接走到 catch 中进行处理
        var loginUser = tryLogin(username, password);
        //走到这里表示 即 "成功获取到了 token" 也 登录成功了 我们将这些信息加入到 TestAuth 里的 ALREADY_LOGIN_CLIENTS 列表中
        var session = new Session(token, loginUser.id, loginDevice);
        SESSION_STORE.add(session);
        //这里根据登录设备向客户端返回不同的信息
        return session;
    }

    /**
     * 尝试使用 用户名和密码进行登录
     *
     * @param username 用户名
     * @param password 密码
     * @return a
     * @throws cool.scx.ext.auth.exception.UnknownUserException   if any.
     * @throws cool.scx.ext.auth.exception.WrongPasswordException if any.
     */
    public U tryLogin(String username, String password) throws UnknownUserException, WrongPasswordException {
        var needLoginUser = userService.get(new Query().equal("username", username));
        //这里标识账号认证成功
        if (needLoginUser == null) {
            throw new UnknownUserException();
        }
        var b = checkPassword(password, needLoginUser.password);
        if (!b) {
            throw new WrongPasswordException(needLoginUser.id);
        }
        return needLoginUser;
    }

    /**
     * a
     *
     * @param username a
     * @param password a
     * @return a
     */
    public abstract U signup(String username, String password);

    /**
     * a
     *
     * @param context a
     * @return a
     */
    public boolean logout(RoutingContext context) {
        return SESSION_STORE.removeByToken(getToken(context));
    }

    /**
     * 通过管理员修改用户密码 (不需要原密码)
     *
     * @param newPassword 新密码
     * @param id          id
     * @return user
     */
    public U changePasswordByAdmin(String newPassword, Long id) {
        var loginUser = getCurrentUser();
        if (loginUser == null) {
            throw new UnauthorizedException("请登录 !!!");
        }
        if (!loginUser.isAdmin) {
            throw new ForbiddenException("非管理员无权限修改用户的用户名 !!!");
        }
        var needChangeUser = userService.get(id);
        //不存在账号报错
        if (needChangeUser == null) {
            throw new UnknownUserException();
        }
        if (StringUtils.isBlank(newPassword)) {
            throw new IllegalArgumentException("新密码不能为空 !!!");
        }
        needChangeUser.password = CryptoUtils.encryptPassword(newPassword.trim());
        return userService.update(needChangeUser, ofIncluded("password"));
    }

    /**
     * 修改当前登录用户的 密码
     *
     * @param newPassword 新密码
     * @param oldPassword 用来校验的密码
     * @return a
     */
    public U changePasswordBySelf(String newPassword, String oldPassword) throws WrongPasswordException, UnknownUserException {
        var loginUser = getCurrentUser();
        if (loginUser == null) {
            throw new UnauthorizedException("请登录 !!!");
        }
        var b = checkPassword(oldPassword, loginUser.password);
        if (!b) {
            throw new WrongPasswordException(loginUser.id);
        }
        var needChangeUser = userService.get(loginUser.id);
        //不存在账号报错
        if (needChangeUser == null) {
            throw new UnknownUserException();
        }
        if (StringUtils.isBlank(newPassword)) {
            throw new IllegalArgumentException("新密码不能为空 !!!");
        }
        needChangeUser.password = CryptoUtils.encryptPassword(newPassword.trim());
        return userService.update(needChangeUser);
    }

    /**
     * 修改当前登录用户的 用户名
     *
     * @param newUsername 新用户名
     * @param password    用来校验的密码
     * @return a
     */
    public U changeUsernameBySelf(String newUsername, String password) throws UnauthorizedException, WrongPasswordException {
        var loginUser = getCurrentUser();
        if (loginUser == null) {
            throw new UnauthorizedException("请登录 !!!");
        }
        var b = checkPassword(password, loginUser.password);
        if (!b) {
            throw new WrongPasswordException(loginUser.id);
        }
        var needChangeUser = checkNeedChangeUserByID(loginUser.id);
        needChangeUser.username = checkNewUsername(newUsername, needChangeUser.id);
        return userService.update(needChangeUser);
    }


    /**
     * 根据 id 获取 用户 和 get 的区别是返回值永远不为空且只包含 [id, password, username] 三个字段
     * 如果对应 id 的用户未找到则抛出移除
     *
     * @param id id
     * @return r
     */
    public U checkNeedChangeUserByID(Long id) {
        var needChangeUser = userService.get(id, ofIncluded().addIncluded("id", "password", "username"));
        //不存在账号报错
        if (needChangeUser == null) {
            throw new UnknownUserException();
        }
        return needChangeUser;
    }

    /**
     * a
     *
     * @return a
     */
    public final Perms getPerms() {
        return getPerms(getCurrentUser());
    }

    /**
     * 根据用户获取 权限串
     *
     * @param user 用户 (这里只会使用用户的唯一标识 所以其他的字段可以为空)
     * @return 权限字符串集合
     */
    public abstract Perms getPerms(U user);

    /**
     * a
     *
     * @param uniqueID    a
     * @param accessToken a
     * @param accountType a
     * @return a
     */
    public abstract U signupByThirdParty(String uniqueID, String accessToken, String accountType);

    /**
     * a
     *
     * @param uniqueID    a
     * @param accessToken a
     * @param accountType a
     * @param ctx         a
     * @return a
     */
    public Session loginByThirdParty(String uniqueID, String accessToken, String accountType, RoutingContext ctx) {
        // 先获取登录的设备类型
        var loginDevice = getDeviceTypeByHeader(ctx);
        // 尝试根据设备类型获取一个可以用来认证的 token
        var token = tryGetAuthToken(ctx, loginDevice);
        // 尝试登录 登录失败会直接走到 catch 中进行处理
        var loginUser = findThirdPartyLoginHandler(accountType).tryLogin(uniqueID, accessToken);
        //走到这里表示 即 "成功获取到了 token" 也 登录成功了 我们将这些信息加入到 TestAuth 里的 ALREADY_LOGIN_CLIENTS 列表中
        var session = new Session(token, loginUser.id, loginDevice);
        SESSION_STORE.add(session);
        //这里根据登录设备向客户端返回不同的信息
        return session;
    }

    /**
     * 根据名称查找第三方的 loginHandler
     *
     * @param type 类型
     * @return handler
     */
    @SuppressWarnings("unchecked")
    public final ThirdPartyLoginHandler<U> findThirdPartyLoginHandler(String type) {
        var thirdPartyLoginHandler = THIRD_PARTY_LOGIN_HANDLER_MAP.get(type);
        if (thirdPartyLoginHandler == null) {
            throw new UnknownLoginHandlerException();
        }
        return (ThirdPartyLoginHandler<U>) thirdPartyLoginHandler;
    }

    /**
     * <p>alreadyLoginClientMap.</p>
     *
     * @return a
     */
    public final SessionStore loggedInClientTable() {
        return SESSION_STORE;
    }

    /**
     * 添加一个 第三方的 loginHandler
     *
     * @param type                   名称
     * @param thirdPartyLoginHandler handler
     */
    public final void addThirdPartyLoginHandler(String type, ThirdPartyLoginHandler<?> thirdPartyLoginHandler) {
        THIRD_PARTY_LOGIN_HANDLER_MAP.put(type, thirdPartyLoginHandler);
    }

    /**
     * 查看当前登录用户是否有对应的权限 此处查看的一般为通用权限
     *
     * @param permString 权限串
     * @return 是否拥有这个权限
     */
    public abstract boolean hasPerm(String permString);

    /**
     * 根据 token 绑定 websocket
     *
     * @param wsParam a {@link java.lang.Object} object
     */
    public void bindWebSocketByToken(WSMessage<?> wsParam) {
        var objectMap = ObjectUtils.convertValue(wsParam.body(), ObjectUtils.MAP_TYPE);
        //获取 token
        var token = ObjectUtils.convertValue(objectMap.get("token"), String.class);
        //判断 token 是否有效
        var client = SESSION_STORE.getByToken(token);
        if (client != null) {
            //这条 websocket 连接所携带的 token 验证通过
            client.webSocket = wsParam.webSocket();
        }
    }

    /**
     * a
     *
     * @return a
     */
    public Session getCurrentSession() {
        return SESSION_STORE.getByToken(getToken(ScxMvc.routingContext()));
    }

    /**
     * <p>getLoginUserByWebSocketID.</p>
     *
     * @param socket a {@link io.vertx.core.http.ServerWebSocket} object
     * @return a T object
     */
    public U getCurrentUserByWebSocket(ServerWebSocket socket) {
        var client = SESSION_STORE.getByWebSocket(socket);
        return client != null ? userService.get(client.userID()) : null;
    }

    /**
     * 从文件中读取 LoginItem
     */
    public void readSessionFromFile() {
        try {
            var data = Files.readAllBytes(SCX_SESSION_CACHE_PATH);
            byte[] jsonBytes = CryptoUtils.decryptBinary(data, ScxContext.appKey());
            var clients = ObjectUtils.jsonMapper().readTree(jsonBytes);
            var i = 0;
            for (JsonNode client : clients) {
                try {
                    SESSION_STORE.add(new Session(
                            client.get("token").textValue(),
                            client.get("userID").longValue(),
                            DeviceType.of(client.get("loginDevice").textValue())));
                    i = i + 1;
                } catch (Exception ignored) {

                }
            }
            Ansi.out().brightGreen("成功从 " + SCX_SESSION_CACHE_PATH + " 中恢复 " + i + " 条 Session 数据!!!").println();
        } catch (Exception ignored) {

        }
    }

    /**
     * 写入 LoginItem 到文件中
     */
    public void writeSessionToFile() {
        var list = SESSION_STORE.loggedInClients().stream().map(c -> {
            var m = new HashMap<>();
            m.put("userID", c.userID());
            m.put("loginDevice", c.loginDevice());
            m.put("token", c.token());
            return m;
        }).toList();
        try {
            byte[] jsonBytes = ObjectUtils.toJson(list).getBytes(StandardCharsets.UTF_8);
            Files.write(SCX_SESSION_CACHE_PATH, CryptoUtils.encryptBinary(jsonBytes, ScxContext.appKey()));
            Ansi.out().red("保存 " + list.size() + " 条 Session 数据到 " + SCX_SESSION_CACHE_PATH + " 中!!!").println();
        } catch (Exception ignored) {

        }
    }

    /**
     * 检查新用户名 (验空和是否重复)
     *
     * @param username username
     * @param id       用户名 (用来校验用户名是否唯一)
     * @return 去除首位空格后的 密码
     */
    public String checkNewUsername(String username, Long id) {
        if (StringUtils.isBlank(username)) {
            throw new IllegalArgumentException("新用户名不能为空 !!!");
        }
        username = username.trim();
        //判断数据库中是否已有重名用户
        var count = userService.count(new Query().equal("username", username).notEqual("id", id, WhereOption.SKIP_IF_NULL));
        if (count != 0) {
            throw new UsernameAlreadyExistsException();
        }
        return username;
    }

    /**
     * 检查新用户名 (验空和是否重复)
     *
     * @param username username
     * @return 去除首位空格后的 密码
     */
    public String checkNewUsername(String username) {
        return checkNewUsername(username, null);
    }

}
