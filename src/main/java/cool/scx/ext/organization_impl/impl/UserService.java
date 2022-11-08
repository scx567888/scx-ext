package cool.scx.ext.organization_impl.impl;

import cool.scx.core.annotation.ScxService;
import cool.scx.ext.organization.base.BaseUserService;
import cool.scx.ext.organization.base.UserDeptService;
import cool.scx.ext.organization.base.UserRoleService;
import cool.scx.sql.base.Query;
import cool.scx.sql.base.SelectFilter;
import cool.scx.util.MultiMap;

import java.util.List;

/**
 * <p>UserService class.</p>
 *
 * @author scx567888
 * @version 1.13.5
 */
@ScxService
public final class UserService extends BaseUserService<User> {

    /**
     * a
     *
     * @param userRoleService a
     * @param userDeptService a
     */
    public UserService(UserRoleService userRoleService, UserDeptService userDeptService) {
        super(userRoleService, userDeptService);
    }

    @Override
    public List<User> list(Query query, SelectFilter selectFilter) {
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
    public List<User> fillDeptIDsAndRoleIDsField(List<User> oldList, Query query) {
        var userIDs = buildListSQLWithAlias(query, SelectFilter.ofIncluded("id"));
        var userDeptList = userDeptService.list(new Query().in("userID", userIDs));
        var userRoleList = userRoleService.list(new Query().in("userID", userIDs));
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

}
