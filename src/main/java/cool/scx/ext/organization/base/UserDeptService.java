package cool.scx.ext.organization.base;

import cool.scx.core.annotation.ScxService;
import cool.scx.core.base.BaseModelService;
import cool.scx.sql.base.Query;

import java.util.List;
import java.util.Objects;

/**
 * 用户 部门 关联表 service 一般不单独使用
 *
 * @author scx567888
 * @version 0.3.6
 */
@ScxService
public final class UserDeptService extends BaseModelService<UserDept> {

    /**
     * <p>deleteByUserID.</p>
     *
     * @param id a {@link java.lang.Long} object
     */
    public void deleteByUserID(Long id) {
        this.delete(new Query().equal("userID", id));
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
            this.add(idArr);
        }
    }

}
