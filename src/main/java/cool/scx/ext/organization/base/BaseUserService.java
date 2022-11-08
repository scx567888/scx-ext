package cool.scx.ext.organization.base;

import cool.scx.core.ScxContext;
import cool.scx.core.base.BaseModelService;

import java.util.List;

/**
 * 核心用户 service
 *
 * @author scx567888
 * @version 1.1.2
 */
public abstract class BaseUserService<T extends BaseUser> extends BaseModelService<T> {

    /**
     * a
     */
    protected final UserRoleService userRoleService;

    /**
     * a
     */
    protected final UserDeptService userDeptService;

    /**
     * 构造函数
     *
     * @param userRoleService a
     * @param userDeptService a
     */
    public BaseUserService(UserRoleService userRoleService, UserDeptService userDeptService) {
        this.userRoleService = userRoleService;
        this.userDeptService = userDeptService;
    }

    /**
     * 保存用户 同时根据用户中的 deptIDs 字段更新 dept表
     *
     * @param user    用户
     * @param deptIDs a {@link java.util.List} object
     * @param roleIDs a {@link java.util.List} object
     * @return a
     */
    public T addWithDeptAndRole(T user, List<Long> deptIDs, List<Long> roleIDs) {
        //这里需要保证事务
        return ScxContext.autoTransaction(() -> {
            var newUser = this.add(user);
            userDeptService.addDeptListWithUserID(newUser.id, deptIDs);
            userRoleService.addRoleListWithUserID(newUser.id, roleIDs);
            return newUser;
        });
    }

    /**
     * 更新 同时根据用户中的 deptIDs 字段更新 dept表
     *
     * @param user    用户
     * @param deptIDs a {@link java.util.List} object
     * @param roleIDs a {@link java.util.List} object
     * @return a
     */
    public T updateWithDeptAndRole(T user, List<Long> deptIDs, List<Long> roleIDs) {
        //这里需要保证事务
        return ScxContext.autoTransaction(() -> {
            //更新就是先删除再保存
            userDeptService.deleteByUserID(user.id);
            userDeptService.addDeptListWithUserID(user.id, deptIDs);
            userRoleService.deleteByUserID(user.id);
            userRoleService.addRoleListWithUserID(user.id, roleIDs);
            return this.update(user);
        });
    }

}
