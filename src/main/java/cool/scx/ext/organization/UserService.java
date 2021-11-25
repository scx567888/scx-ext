package cool.scx.ext.organization;

import cool.scx.annotation.ScxService;
import cool.scx.base.BaseService;
import cool.scx.bo.Query;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 核心用户 service
 *
 * @author scx567888
 * @version 1.1.2
 */
@ScxService
public class UserService extends BaseService<User> {

    /**
     * 部门 service
     */
    private final DeptService deptService;

    /**
     * 角色 service
     */
    private final RoleService roleService;


    /**
     * c
     *
     * @param deptService c
     * @param roleService c
     */
    public UserService(DeptService deptService, RoleService roleService) {
        this.deptService = deptService;
        this.roleService = roleService;
    }

    /**
     * {@inheritDoc}
     *
     * @param uniqueID a {@link java.lang.String} object
     * @return a {@link User} object
     */
    public User getByUsername(String uniqueID) {
        return get(new Query().equal("username", uniqueID));
    }

    /**
     * {@inheritDoc}
     * <p>
     * 重写方法
     *
     * @param query a {@link cool.scx.bo.Query} object
     * @return a {@link java.util.List} object
     */
    public List<User> listWithRoleAndDept(Query query) {
        List<User> userList = super.list(query);
        var userIDs = userList.stream().map(user -> user.id).collect(Collectors.toList());
        var userDeptListFuture = CompletableFuture.supplyAsync(() -> deptService.getUserDeptByUserIDs(userIDs));
        var userRoleListFuture = CompletableFuture.supplyAsync(() -> roleService.getUserRoleByUserIDs(userIDs));
        try {
            var userDeptList = userDeptListFuture.get();
            var userRoleList = userRoleListFuture.get();
            return userList.stream().peek(item -> {
                item.deptIDs = userDeptList.stream().filter(userDept -> userDept.userID.equals(item.id)).map(deptItem -> deptItem.deptID).collect(Collectors.toList());
                item.roleIDs = userRoleList.stream().filter(userRole -> userRole.userID.equals(item.id)).map(deptItem -> deptItem.roleID).collect(Collectors.toList());
            }).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
