package cool.scx.ext.organization.base;

import cool.scx.core.base.BaseModelService;
import cool.scx.core.base.Query;
import cool.scx.core.base.SelectFilter;
import cool.scx.sql.SQL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * <p>CoreDeptService class.</p>
 *
 * @author scx567888
 * @version 1.1.2
 */
public abstract class BaseDeptService<T extends BaseDept> extends BaseModelService<T> {

    private final UserDeptService userDeptService;

    /**
     * <p>Constructor for CoreDeptService.</p>
     *
     * @param userDeptService a {@link cool.scx.ext.organization.base.UserDeptService} object.
     */
    public BaseDeptService(UserDeptService userDeptService) {
        this.userDeptService = userDeptService;
    }

    /**
     * getDeptListByUser
     *
     * @param user a {@link cool.scx.ext.organization.base.BaseUser} object
     * @return a {@link java.util.List} object
     */
    public List<T> getDeptListByUser(BaseUser user) {
        var deptIDs = userDeptService.buildListSQL(new Query().equal("userID", user.id), SelectFilter.ofIncluded("deptID"));
        return list(new Query().in("id", deptIDs));
    }

    /**
     * saveDeptListWithUserID
     *
     * @param userID  a {@link java.lang.Long} object
     * @param deptIDs a {@link java.lang.String} object
     */
    public void addDeptListWithUserID(Long userID, List<Long> deptIDs) {
        if (deptIDs != null) {
            var idArr = deptIDs.stream().filter(Objects::nonNull).map(id -> {
                var userDept = new UserDept();
                userDept.userID = userID;
                userDept.deptID = id;
                return userDept;
            }).toList();
            userDeptService.add(idArr);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param id a {@link java.lang.Long} object
     */
    public void deleteByUserID(Long id) {
        userDeptService.delete(new Query().equal("userID", id));
    }

    /**
     * {@inheritDoc}
     *
     * @param userID a {@link java.lang.Long} object
     * @return a {@link java.util.List} object
     */
    public List<UserDept> findDeptByUserID(Long userID) {
        if (userID != null) {
            return userDeptService.list(new Query().equal("userID", userID));
        }
        return new ArrayList<>();
    }

    /**
     * <p>getUserDeptByUserIDs.</p>
     *
     * @param userIDs a
     * @return a {@link java.util.List} object
     */
    public List<UserDept> getUserDeptByUserIDs(SQL userIDs) {
        return userDeptService.list(new Query().in("userID", userIDs));
    }

    /**
     * 递归删除 部门节点
     *
     * @param id id
     */
    public void deleteDeptWithChildren(Long id) {
        var deptWithChildren = getDeptWithChildren(id);
        delete(deptWithChildren.stream().mapToLong(dept -> dept.id).toArray());
    }

    /**
     * 递归查询 dept
     *
     * @param id id
     * @return r
     */
    public List<BaseDept> getDeptWithChildren(Long id) {
        //获取此部门下所有的子集部门的 id
        var deptWithChildren = new ArrayList<BaseDept>();
        _fillDeptWithChildren(deptWithChildren, get(id));
        return deptWithChildren;
    }

    /**
     * 内部方法 : 用于根据 parentDept 执行递归填充数据,
     *
     * @param deptWithChildren d
     * @param parentDept       d
     */
    private void _fillDeptWithChildren(Collection<BaseDept> deptWithChildren, BaseDept parentDept) {
        if (parentDept != null) {
            //先将父 id 放入集合中
            deptWithChildren.add(parentDept);
            //找一下当前节点的子节点 这里做一个小优化 因为 我们只需要 id 所以这里之查询 id (使用 SelectFilter)
            list(new Query().equal("parentID", parentDept.id),
                    SelectFilter.ofIncluded().addIncluded("id")
            ).forEach(child -> _fillDeptWithChildren(deptWithChildren, child));
        }
    }

}
