package cool.scx.ext.organization.base;

import cool.scx.core.base.BaseModelService;
import cool.scx.core.base.Query;
import cool.scx.core.base.SelectFilter;
import cool.scx.sql.SQL;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * RoleService
 *
 * @author scx567888
 * @version 1.1.2
 */
public abstract class BaseRoleService<T extends BaseRole> extends BaseModelService<T> {

    private final UserRoleService userRoleService;

    /**
     * <p>Constructor for CoreRoleService.</p>
     *
     * @param userRoleService a {@link cool.scx.ext.organization.base.UserRoleService} object.
     */
    public BaseRoleService(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    /**
     * 根据 用户获取 角色
     *
     * @param user a {@link cool.scx.ext.organization.base.BaseUser} object
     * @return a {@link java.util.List} object
     */
    public List<T> getRoleListByUser(BaseUser user) {
        var roleIDs = userRoleService.buildListSQL(new Query().equal("userID", user.id), SelectFilter.ofIncluded("roleID"));
        return list(new Query().in("id", roleIDs));
    }

    /**
     * getUserRoleByUserIDs
     *
     * @param userIDs a
     * @return a {@link java.util.List} object
     */
    public List<UserRole> getUserRoleByUserIDs(SQL userIDs) {
        return userRoleService.list(new Query().in("userID", userIDs));
    }

    /**
     * <p>findDeptByUserID.</p>
     *
     * @param userID a {@link java.lang.Long} object
     * @return a {@link java.util.List} object
     */
    public List<UserRole> findDeptByUserID(Long userID) {
        if (userID != null) {
            return userRoleService.list(new Query().equal("userID", userID));
        }
        return new ArrayList<>();
    }

    /**
     * saveRoleListWithUserID
     *
     * @param userID  a {@link java.lang.Long} object
     * @param roleIDs a {@link java.lang.String} object
     */
    public void addRoleListWithUserID(Long userID, List<Long> roleIDs) {
        if (roleIDs != null) {
            var idArr = roleIDs.stream().filter(Objects::nonNull).map(id -> {
                var userRole = new UserRole();
                userRole.userID = userID;
                userRole.roleID = id;
                return userRole;
            }).toList();
            userRoleService.add(idArr);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param id a {@link java.lang.Long} object
     */
    public void deleteByUserID(Long id) {
        userRoleService.delete(new Query().equal("userID", id));
    }

    /**
     * 根据用户查询是否有对应的权限 一般不直接调用 请使用 {@link cool.scx.ext.organization.auth.ScxAuth#hasPerm(String)}
     *
     * @param loginUser  用户
     * @param permString 权限串
     * @return 是否拥有这个权限
     */
    public boolean hasPerm(BaseUser loginUser, String permString) {
        if (loginUser.isAdmin) {//管理员拥有全部权限
            return true;
        }
        var roleIDs = userRoleService.buildListSQL(new Query().equal("userID", loginUser.id), SelectFilter.ofIncluded("roleID"));
        var c = count(new Query().in("id", roleIDs).jsonContains("perms", permString));
        return c > 0;
    }

}
