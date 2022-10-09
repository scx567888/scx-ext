package cool.scx.ext.organization.base;

import cool.scx.core.ScxContext;
import cool.scx.core.base.BaseModelService;
import cool.scx.core.http.exception.impl.NoPermException;
import cool.scx.core.http.exception.impl.UnauthorizedException;
import cool.scx.ext.organization.auth.ScxAuth;
import cool.scx.ext.organization.exception.UnknownUserException;
import cool.scx.ext.organization.exception.UsernameAlreadyExistsException;
import cool.scx.ext.organization.exception.WrongPasswordException;
import cool.scx.sql.base.Query;
import cool.scx.sql.base.SelectFilter;
import cool.scx.sql.base.UpdateFilter;
import cool.scx.sql.where.WhereOption;
import cool.scx.util.CryptoUtils;
import cool.scx.util.MultiMap;
import cool.scx.util.NetUtils;
import cool.scx.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 核心用户 service
 *
 * @author scx567888
 * @version 1.1.2
 */
public abstract class BaseUserService<T extends BaseUser> extends BaseModelService<T> {

    /**
     * Constant <code>logger</code>
     */
    private static final Logger logger = LoggerFactory.getLogger(BaseUserService.class);

    /**
     * a
     */
    private final BaseDeptService<?> deptService;

    /**
     * a
     */
    private final BaseRoleService<?> roleService;

    /**
     * <p>Constructor for UserService.</p>
     *
     * @param deptService a {@link cool.scx.ext.organization.base.BaseDeptService} object
     * @param roleService a {@link cool.scx.ext.organization.base.BaseRoleService} object
     */
    public BaseUserService(BaseDeptService<?> deptService, BaseRoleService<?> roleService) {
        this.deptService = deptService;
        this.roleService = roleService;
    }

    /**
     * 保存用户 同时根据用户中的 deptIDs 字段更新 dept表
     *
     * @param user 用户
     * @return a
     */
    public T addWithDeptAndRole(T user) {
        user.password = encryptPassword(user.password);
        //这里需要保证事务
        return ScxContext.autoTransaction(() -> {
            var newUser = this.add(user);
            deptService.addDeptListWithUserID(newUser.id, user.deptIDs);
            roleService.addRoleListWithUserID(newUser.id, user.roleIDs);
            //并不直接返回 newUser 而是重新 get 获取是为了填充 deptIDs 和 roleIDs 字段
            return get(newUser.id);
        });
    }

    /**
     * 更新 同时根据用户中的 deptIDs 字段更新 dept表
     *
     * @param user 用户
     * @return a
     */
    public T updateWithDeptAndRole(T user) {
        user.password = encryptPassword(user.password);
        //这里需要保证事务
        return ScxContext.autoTransaction(() -> {
            //更新就是先删除再保存
            deptService.deleteByUserID(user.id);
            deptService.addDeptListWithUserID(user.id, user.deptIDs);
            roleService.deleteByUserID(user.id);
            roleService.addRoleListWithUserID(user.id, user.roleIDs);
            return this.update(user);
        });
    }

    /**
     * 检查系统中是否存在至少有一个管理员
     *
     * @param id id
     * @return a boolean
     */
    public boolean checkThatThereIsAtLeastOneAdmin(Long id) {
        var count = count(new Query().equal("isAdmin", true).notEqual("id", id, WhereOption.SKIP_IF_NULL));
        return count != 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> list(Query query, SelectFilter selectFilter) {
        return fillDeptIDsAndRoleIDsField(super.list(query, selectFilter), query);
    }

    /**
     * {@inheritDoc}
     * <p>
     * 重写方法
     *
     * @param oldList a {@link cool.scx.sql.base.Query} object
     * @param query   q
     * @return a {@link java.util.List} object
     */
    public List<T> fillDeptIDsAndRoleIDsField(List<T> oldList, Query query) {
        var userIDs = buildListSQLWithAlias(query, SelectFilter.ofIncluded("id"));
        var userDeptList = deptService.getUserDeptByUserIDs(userIDs);
        var userRoleList = roleService.getUserRoleByUserIDs(userIDs);
        MultiMap<Long, Long> userIDAndDeptIDMap = new MultiMap<>();
        MultiMap<Long, Long> userIDAndRoleIDMap = new MultiMap<>();
        for (var userDept : userDeptList) {
            userIDAndDeptIDMap.put(userDept.userID, userDept.deptID);
        }
        for (var userRole : userRoleList) {
            userIDAndRoleIDMap.put(userRole.userID, userRole.roleID);
        }
        return oldList.stream().peek(item -> {
            item.deptIDs = userIDAndDeptIDMap.get(item.id);
            item.roleIDs = userIDAndRoleIDMap.get(item.id);
        }).toList();
    }

    /**
     * 通过管理员修改用户密码 (不需要原密码)
     *
     * @param newPassword 新密码
     * @param id          id
     * @return r
     */
    public final T changePasswordByAdminUser(String newPassword, Long id) {
        checkNowLoginUserIsAdmin();
        var needChangeUser = checkNeedChangeUserByID(id);
        needChangeUser.password = CryptoUtils.encryptPassword(checkNewPasswordStr(newPassword));
        return update(needChangeUser);
    }

    /**
     * 修改当前登录用户的 密码
     *
     * @param newPassword 新密码
     * @param password    用来校验的密码
     * @return a
     */
    public final BaseUser changePasswordBySelf(String newPassword, String password) {
        var loginUser = checkNowLoginUser();
        checkPassword(loginUser, password);
        var needChangeUser = checkNeedChangeUserByID(loginUser.id);
        needChangeUser.password = CryptoUtils.encryptPassword(checkNewPasswordStr(newPassword));
        return update(needChangeUser);
    }

    /**
     * 修改当前登录用户的 用户名
     *
     * @param newUsername 新用户名
     * @param password    用来校验的密码
     * @return a
     */
    public final BaseUser changeUsernameBySelf(String newUsername, String password) {
        var loginUser = checkNowLoginUser();
        checkPassword(loginUser, password);
        var needChangeUser = checkNeedChangeUserByID(loginUser.id);
        needChangeUser.username = checkNewUsernameStr(newUsername, needChangeUser.id);
        return update(needChangeUser);
    }

    /**
     * 根据 id 获取 用户 和 get 的区别是返回值永远不为空且只包含 [id, password, username] 三个字段
     * 如果对应 id 的用户未找到则抛出移除
     *
     * @param id id
     * @return r
     */
    public final T checkNeedChangeUserByID(Long id) {
        var needChangeUser = get(id, SelectFilter.ofIncluded().addIncluded("id", "password", "username"));
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
    public final String checkNewUsernameStr(String username, Long id) {
        if (StringUtils.isBlank(username)) {
            throw new IllegalArgumentException("新用户名不能为空 !!!");
        }
        username = username.trim();
        //判断数据库中是否已有重名用户
        var count = count(new Query().equal("username", username).notEqual("id", id));
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
    public final String checkNewUsernameStr(String username) {
        if (StringUtils.isBlank(username)) {
            throw new IllegalArgumentException("新用户名不能为空 !!!");
        }
        username = username.trim();
        //判断数据库中是否已有重名用户
        var count = count(new Query().equal("username", username));
        if (count != 0) {
            throw new UsernameAlreadyExistsException();
        }
        return username;
    }

    /**
     * 检查新密码 (只验空)
     *
     * @param password password
     * @return 去除首位空格后的 密码
     */
    public final String checkNewPasswordStr(String password) {
        if (StringUtils.isBlank(password)) {
            throw new IllegalArgumentException("新密码不能为空 !!!");
        }
        return password.trim();
    }

    /**
     * 检查当前用户是不是管理员
     *
     * @return 登录的用户
     */
    public final BaseUser checkNowLoginUserIsAdmin() {
        var loginUser = checkNowLoginUser();
        if (!loginUser.isAdmin) {
            throw new NoPermException("非管理员无权限修改用户的用户名 !!!");
        }
        return loginUser;
    }

    /**
     * 检查当前登录用户
     *
     * @return user
     */
    public final BaseUser checkNowLoginUser() {
        var loginUser = ScxAuth.getLoginUser();
        if (loginUser == null) {
            throw new UnauthorizedException("请登录 !!!");
        }
        return loginUser;
    }

    /**
     * 校验密码
     *
     * @param user     用户(需要保护密码字段)
     * @param password 前台发过来的密码
     */
    public final void checkPassword(BaseUser user, String password) {
        boolean b;
        try {
            b = CryptoUtils.checkPassword(password, user.password);
        } catch (Exception e) {
            logger.error("验证用户密码时出现解码错误 !!!", e);
            throw new WrongPasswordException();
        }
        if (!b) {
            throw new WrongPasswordException();
        }
    }

    /**
     * 尝试使用 用户名和密码进行登录
     *
     * @param username 用户名
     * @param password 密码
     * @return a {@link cool.scx.ext.organization.base.BaseUser} object
     */
    public BaseUser tryLogin(String username, String password) {
        var needLoginUser = get(new Query().equal("username", username));
        //这里标识账号认证成功
        if (needLoginUser == null) {
            throw new UnknownUserException();
        }
        checkPassword(needLoginUser, password);
        return needLoginUser;
    }

    /**
     * <p>tryLoginByEmailAddress.</p>
     *
     * @param emailAddress     a {@link java.lang.String} object
     * @param verificationCode a {@link java.lang.String} object
     * @return a {@link cool.scx.ext.organization.base.BaseUser} object
     */
    public BaseUser tryLoginByEmailAddress(String emailAddress, String verificationCode) {
        throw new RuntimeException("暂未实现此种登录方式");
    }

    /**
     * <p>tryLoginByPhoneNumber.</p>
     *
     * @param phoneNumber      a {@link java.lang.String} object
     * @param verificationCode a {@link java.lang.String} object
     * @return a {@link cool.scx.ext.organization.base.BaseUser} object
     */
    public BaseUser tryLoginByPhoneNumber(String phoneNumber, String verificationCode) {
        throw new RuntimeException("暂未实现此种登录方式");
    }

    /**
     * <p>encryptPassword.</p>
     *
     * @param plainPassword a {@link java.lang.String} object
     * @return a {@link java.lang.String} object
     */
    public String encryptPassword(String plainPassword) {
        if (StringUtils.notBlank(plainPassword)) {
            return CryptoUtils.encryptPassword(plainPassword.trim());
        } else {
            return null;
        }
    }

    /**
     * 更新用户的最后一次登录的 时间和ip
     *
     * @param user        用户
     * @param accountType 类型
     */
    public void updateLastLoginDateAndIP(BaseUser user, String accountType) {
        var oldUser = get(user.id);
        var history = oldUser.loginInfoHistory;
        if (history == null) {
            history = new ArrayList<>();
        }
        var ip = NetUtils.getClientIPAddress(ScxContext.routingContext().request());
        var date = LocalDateTime.now();
        history.add(new BaseUser.LoginInfo(ip, date, accountType));
        //只取最后 10 次
        oldUser.loginInfoHistory = history.subList(Math.max(history.size() - 10, 0), history.size());
        update(oldUser, new Query().equal("id", oldUser.id), UpdateFilter.ofIncluded("loginInfoHistory"));
    }

    /**
     * 注册用户
     *
     * @param user 用户信息 需要包含 用户名和密码(明文类型)
     * @return user
     */
    public T signup(T user) {
        user.username = checkNewUsernameStr(user.username);
        user.password = CryptoUtils.encryptPassword(checkNewPasswordStr(user.password));
        user.isAdmin = false;
        return add(user);
    }

}
