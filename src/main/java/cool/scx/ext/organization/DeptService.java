package cool.scx.ext.organization;

import cool.scx.annotation.ScxService;
import cool.scx.base.BaseService;
import cool.scx.bo.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>CoreDeptService class.</p>
 *
 * @author scx567888
 * @version 1.1.2
 */
@ScxService
public class DeptService extends BaseService<Dept> {

    private final UserDeptService userDeptService;

    /**
     * <p>Constructor for CoreDeptService.</p>
     *
     * @param userDeptService a {@link cool.scx.ext.organization.UserDeptService} object.
     */
    public DeptService(UserDeptService userDeptService) {
        this.userDeptService = userDeptService;
    }

    /**
     * getDeptListByUser
     *
     * @param user a {@link cool.scx.ext.organization.User} object
     * @return a {@link java.util.List} object
     */
    public List<Dept> getDeptListByUser(User user) {

        var deptIDs = userDeptService.list(new Query().equal("userID", user.id))
                .stream().map(userRole -> userRole.deptID).toList();
        if (deptIDs.size() > 0) {
            return list(new Query().in("id", deptIDs));
        }

        return new ArrayList<>();
    }

    /**
     * saveDeptListWithUserID
     *
     * @param userID  a {@link java.lang.Long} object
     * @param deptIDs a {@link java.lang.String} object
     */
    public void saveDeptListWithUserID(Long userID, List<Long> deptIDs) {
        if (deptIDs != null) {
            var idArr = deptIDs.stream().filter(Objects::nonNull).map(id -> {
                        var userDept = new UserDept();
                        userDept.userID = userID;
                        userDept.deptID = id;
                        return userDept;
                    }
            ).collect(Collectors.toList());
            userDeptService.save(idArr);
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
     * @param userIDs a {@link java.util.List} object
     * @return a {@link java.util.List} object
     */
    public List<UserDept> getUserDeptByUserIDs(List<Long> userIDs) {
        if (userIDs.size() > 0) {
            return userDeptService.list(new Query().in("userID", userIDs));
        }
        return new ArrayList<>();
    }
}
