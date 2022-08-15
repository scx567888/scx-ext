package cool.scx.ext.organization.role;

import cool.scx.core.base.BaseModelService;
import cool.scx.core.base.Query;
import cool.scx.core.base.SelectFilter;
import cool.scx.ext.organization.user.BaseUser;
import cool.scx.sql.AbstractPlaceholderSQL;

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
     * @param userRoleService a {@link cool.scx.ext.organization.role.UserRoleService} object.
     */
    public BaseRoleService(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    /**
     * 根据 用户获取 角色
     *
     * @param user a {@link BaseUser} object
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
    public List<UserRole> getUserRoleByUserIDs(AbstractPlaceholderSQL<?> userIDs) {
        return userRoleService.list(new Query().in("userID", userIDs));
    }


    /**
     * saveRoleListWithUserID
     *
     * @param userID  a {@link java.lang.Long} object
     * @param roleIDs a {@link java.lang.String} object
     */
    public void saveRoleListWithUserID(Long userID, List<Long> roleIDs) {
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

}
