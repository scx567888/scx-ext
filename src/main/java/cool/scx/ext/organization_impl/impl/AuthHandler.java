package cool.scx.ext.organization_impl.impl;

import cool.scx.core.ScxContext;
import cool.scx.core.http.exception.NoPermException;
import cool.scx.ext.organization.auth.ThirdPartyLoginHandler;
import cool.scx.ext.organization.base.BaseAuthHandler;
import cool.scx.ext.organization.base.BaseUser;
import cool.scx.ext.organization.base.UserDeptService;
import cool.scx.ext.organization.base.UserRoleService;
import cool.scx.ext.organization.exception.UnknownUserException;
import cool.scx.ext.organization.exception.UsernameAlreadyExistsException;
import cool.scx.sql.base.Query;
import cool.scx.sql.base.SelectFilter;
import cool.scx.sql.base.UpdateFilter;
import cool.scx.sql.where.WhereOption;
import cool.scx.util.CryptoUtils;
import cool.scx.util.NetUtils;
import cool.scx.util.RandomUtils;
import cool.scx.util.StringUtils;
import io.vertx.ext.web.RoutingContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static cool.scx.ext.organization.auth.AuthHelper.*;

/**
 * 提供基本的认证逻辑
 * todo 这里的用户登录信息应该做一个缓存 防止多次从数据库中读取
 *
 * @author scx567888
 * @version 1.1.4
 */
@Component
public final class AuthHandler extends BaseAuthHandler<User> {

    public AuthHandler(UserService userService, RoleService roleService, DeptService deptService, UserDeptService userDeptService, UserRoleService userRoleService) {
        super(userService, roleService, deptService, userDeptService, userRoleService);
    }

    /**
     * 检查新密码 (只验空)
     *
     * @param password password
     * @return 去除首位空格后的 密码
     */
    public static String checkNewPasswordStr(String password) {
        if (StringUtils.isBlank(password)) {
            throw new IllegalArgumentException("新密码不能为空 !!!");
        }
        return password.trim();
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
    public User signupByThirdParty(String uniqueID, String accessToken, String accountType) {
        var defaultNewUser = new User();
        //默认用户名
        defaultNewUser.username = "scx_" + RandomUtils.randomString(8, true);
        defaultNewUser.isAdmin = false;
        var handler = (ThirdPartyLoginHandler<User>) findThirdPartyLoginHandler(accountType);
        return handler.signup(uniqueID, accessToken, defaultNewUser);
    }

    /**
     * 注册用户 (默认不为管理员)
     *
     * @param username 用户名
     * @param password 密码(明文类型)
     * @return 新用户
     */
    @Override
    public User signup(String username, String password) {
        return signup(username, password, false);
    }

    /**
     * 注册用户
     *
     * @param username 用户名
     * @param password 密码(明文类型)
     * @param isAdmin  是否为管理员
     * @return 新用户
     */
    public User signup(String username, String password, boolean isAdmin) {
        var user = new User();
        user.username = checkNewUsernameStr(username);
        user.password = CryptoUtils.encryptPassword(checkNewPasswordStr(password));
        user.isAdmin = isAdmin;
        return userService.add(user);
    }

    /**
     * 检查新用户名 (验空和是否重复)
     *
     * @param username username
     * @return 去除首位空格后的 密码
     */
    public String checkNewUsernameStr(String username) {
        if (StringUtils.isBlank(username)) {
            throw new IllegalArgumentException("新用户名不能为空 !!!");
        }
        username = username.trim();
        //判断数据库中是否已有重名用户
        var count = userService.count(new Query().equal("username", username));
        if (count != 0) {
            throw new UsernameAlreadyExistsException();
        }
        return username;
    }

    /**
     * 通过管理员修改用户密码 (不需要原密码)
     *
     * @param newPassword 新密码
     * @param id          id
     * @return r
     */
    public User changePasswordByAdmin(String newPassword, Long id) {
        checkNowLoginUserIsAdmin();
        var needChangeUser = checkNeedChangeUserByID(id);
        needChangeUser.password = CryptoUtils.encryptPassword(checkNewPasswordStr(newPassword));
        return userService.update(needChangeUser);
    }

    /**
     * 修改当前登录用户的 密码
     *
     * @param newPassword 新密码
     * @param password    用来校验的密码
     * @return a
     */
    @Override
    public User changePasswordBySelf(String newPassword, String password) {
        var loginUser = checkCurrentUserOrThrow();
        checkPasswordOrThrow(password, loginUser.password);
        var needChangeUser = checkNeedChangeUserByID(loginUser.id);
        needChangeUser.password = CryptoUtils.encryptPassword(checkNewPasswordStr(newPassword));
        return userService.update(needChangeUser);
    }

    /**
     * 检查系统中是否存在至少有一个管理员
     *
     * @param id id
     * @return a boolean
     */
    public boolean checkThatThereIsAtLeastOneAdmin(Long id) {
        var count = userService.count(new Query().equal("isAdmin", true).notEqual("id", id, WhereOption.SKIP_IF_NULL));
        return count != 0;
    }

    /**
     * 修改当前登录用户的 用户名
     *
     * @param newUsername 新用户名
     * @param password    用来校验的密码
     * @return a
     */
    public User changeUsernameBySelf(String newUsername, String password) {
        var loginUser = checkCurrentUserOrThrow();
        checkPasswordOrThrow(password, loginUser.password);
        var needChangeUser = checkNeedChangeUserByID(loginUser.id);
        needChangeUser.username = checkNewUsernameStr(newUsername, needChangeUser.id);
        return userService.update(needChangeUser);
    }

    /**
     * 根据 id 获取 用户 和 get 的区别是返回值永远不为空且只包含 [id, password, username] 三个字段
     * 如果对应 id 的用户未找到则抛出移除
     *
     * @param id id
     * @return r
     */
    public User checkNeedChangeUserByID(Long id) {
        var needChangeUser = userService.get(id, SelectFilter.ofIncluded().addIncluded("id", "password", "username"));
        //不存在账号报错
        if (needChangeUser == null) {
            throw new UnknownUserException();
        }
        return needChangeUser;
    }

    /**
     * 检查新用户名 (验空和是否重复)
     *
     * @param username username
     * @param id       用户名 (用来校验用户名是否唯一)
     * @return 去除首位空格后的 密码
     */
    public String checkNewUsernameStr(String username, Long id) {
        if (StringUtils.isBlank(username)) {
            throw new IllegalArgumentException("新用户名不能为空 !!!");
        }
        username = username.trim();
        //判断数据库中是否已有重名用户
        var count = userService.count(new Query().equal("username", username).notEqual("id", id));
        if (count != 0) {
            throw new UsernameAlreadyExistsException();
        }
        return username;
    }

    /**
     * 检查当前用户是不是管理员
     *
     * @return 登录的用户
     */
    public BaseUser checkNowLoginUserIsAdmin() {
        var loginUser = checkCurrentUserOrThrow();
        if (!loginUser.isAdmin) {
            throw new NoPermException("非管理员无权限修改用户的用户名 !!!");
        }
        return loginUser;
    }

    @Override
    public String login(String username, String password, RoutingContext ctx) {
        // 先获取登录的设备类型
        var loginDevice = getDeviceTypeByHeader(ctx);
        // 尝试根据设备类型获取一个可以用来认证的 token
        var token = tryGetAuthToken(ctx, loginDevice);
        // 尝试登录 登录失败会直接走到 catch 中进行处理
        var loginUser = tryLogin(username, password);
        //走到这里表示 即 "成功获取到了 token" 也 登录成功了 我们将这些信息加入到 TestAuth 里的 ALREADY_LOGIN_CLIENTS 列表中
        addLoginItem(token, loginUser, loginDevice);
        //更新
        updateLastLoginDateAndIP(loginUser, "PASSWORD");
        //这里根据登录设备向客户端返回不同的信息
        return token;
    }

    @Override
    public String loginByThirdParty(String uniqueID, String accessToken, String accountType, RoutingContext ctx) {
        // 先获取登录的设备类型
        var loginDevice = getDeviceTypeByHeader(ctx);
        // 尝试根据设备类型获取一个可以用来认证的 token
        var token = tryGetAuthToken(ctx, loginDevice);
        // 尝试登录 登录失败会直接走到 catch 中进行处理
        var loginUser = findThirdPartyLoginHandler(accountType).tryLogin(uniqueID, accessToken);
        //走到这里表示 即 "成功获取到了 token" 也 登录成功了 我们将这些信息加入到 TestAuth 里的 ALREADY_LOGIN_CLIENTS 列表中
        addLoginItem(token, loginUser, loginDevice);
        //更新
        updateLastLoginDateAndIP(loginUser, accountType);
        //这里根据登录设备向客户端返回不同的信息
        return token;
    }


    /**
     * 更新用户的最后一次登录的 时间和ip
     *
     * @param user        用户
     * @param accountType 类型
     */
    public void updateLastLoginDateAndIP(BaseUser user, String accountType) {
        var oldUser = userService.get(user.id);
        var history = oldUser.loginInfoHistory;
        if (history == null) {
            history = new ArrayList<>();
        }
        var ip = NetUtils.getClientIPAddress(ScxContext.routingContext().request());
        var date = LocalDateTime.now();
        history.add(new User.LoginInfo(ip, date, accountType));
        //只取最后 10 次
        oldUser.loginInfoHistory = history.subList(Math.max(history.size() - 10, 0), history.size());
        userService.update(oldUser, new Query().equal("id", oldUser.id), UpdateFilter.ofIncluded("loginInfoHistory"));
    }

}
