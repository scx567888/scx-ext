package cool.scx.ext.organization.base;

import cool.scx.core.annotation.ScxService;
import cool.scx.core.base.BaseModelService;
import cool.scx.sql.base.Query;

import java.util.List;
import java.util.Objects;

/**
 * 用户 角色 关联表 service 一般不单独使用
 *
 * @author scx567888
 * @version 0.3.6
 */
@ScxService
public final class UserRoleService extends BaseModelService<UserRole> {

    /**
     * {@inheritDoc}
     *
     * @param id a {@link java.lang.Long} object
     */
    public void deleteByUserID(Long id) {
        this.delete(new Query().equal("userID", id));
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
            this.add(idArr);
        }
    }

}
