package cool.scx.ext.organization;

import cool.scx.annotation.ScxService;
import cool.scx.base.BaseModelService;
import cool.scx.bo.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
     * @param userRoleService a {@link cool.scx.ext.organization.UserRoleService} object.
     */
    public RoleService(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    /**
     * getRoleListByUser
     *
     * @param user a {@link cool.scx.ext.organization.User} object
     * @return a {@link java.util.List} object
     */
    public List<Role> getRoleListByUser(User user) {
        var roleIDs = userRoleService.list(new Query().equal("userID", user.id))
                .stream().map(userRole -> userRole.roleID).toList();
        if (roleIDs.size() > 0) {
            return list(new Query().in("id", roleIDs));
        }
        return new ArrayList<>();
    }

    /**
     * getUserRoleByUserIDs
     *
     * @param userIDs a {@link cool.scx.ext.organization.User} object
     * @return a {@link java.util.List} object
     */
    public List<UserRole> getUserRoleByUserIDs(List<Long> userIDs) {
        if (userIDs.size() > 0) {
            return userRoleService.list(new Query().in("userID", userIDs));
        }
        return new ArrayList<>();
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
                    }
            ).collect(Collectors.toList());
            userRoleService.save(idArr);
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
