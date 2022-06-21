package cool.scx.ext.organization.role;

import cool.scx.annotation.ScxService;
import cool.scx.base.BaseModelService;
import cool.scx.base.Query;
import cool.scx.base.SelectFilter;
import cool.scx.ext.organization.user.User;
import cool.scx.sql.AbstractPlaceholderSQL;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * RoleService
 *
 * @author scx567888
 * @version 1.1.2
 */
@ScxService
public class RoleService extends BaseModelService<Role> {

    private final UserRoleService userRoleService;

    /**
     * <p>Constructor for CoreRoleService.</p>
     *
     * @param userRoleService a {@link cool.scx.ext.organization.role.UserRoleService} object.
     */
    public RoleService(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    /**
     * 根据 用户获取 角色
     *
     * @param user a {@link cool.scx.ext.organization.user.User} object
     * @return a {@link java.util.List} object
     */
    public List<Role> getRoleListByUser(User user) {
        var roleIDs = userRoleService.buildListSQL(new Query().equal("userID", user.id), SelectFilter.ofIncluded("roleID"));
        return list(new Query().in("id", roleIDs)) ;
    }

    /**
     * getUserRoleByUserIDs
     *
     * @param id
     * @return a {@link java.util.List} object
     */
    public List<UserRole> getUserRoleByUserIDs(AbstractPlaceholderSQL<?> id) {
        return userRoleService.list(new Query().in("userID", id));
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
