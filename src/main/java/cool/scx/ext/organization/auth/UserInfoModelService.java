package cool.scx.ext.organization.auth;

import cool.scx.base.BaseModelService;
import cool.scx.base.Query;
import cool.scx.base.SelectFilter;
import cool.scx.ext.organization.user.User;
import cool.scx.ext.organization.user.UserService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class UserInfoModelService<M extends UserInfoModel> extends BaseModelService<M> {

    protected final UserService userService;

    protected UserInfoModelService(UserService userService) {
        this.userService = userService;
    }

    /**
     * 重写 list 方法以使用 userID 填充 user 对象
     *
     * @param query        query
     * @param selectFilter selectFilter
     * @return 填充后的list
     */
    @Override
    public List<M> list(Query query, SelectFilter selectFilter) {
        return fillUserField(super.list(query, selectFilter));
    }

    /**
     * 根据用户查询 信息
     *
     * @param user user
     * @return list
     */
    public final M getByUser(User user) {
        if (user != null) {
            return get(new Query().equal("userID", user.id));
        }
        return null;
    }

    public final M getByUserWithoutUserField(User user) {
        if (user != null) {
            var list = super.list(new Query().equal("userID", user.id).setPagination(1), SelectFilter.ofExcluded());
            return list.size() > 0 ? list.get(0) : null;
        }
        return null;
    }

    /**
     * 根据用户查询 信息
     *
     * @param userList userList
     * @return list
     */
    public final List<M> listByUser(List<User> userList) {
        var userIDs = userList.stream().map(user -> user.id).toArray();
        return list(new Query().in("userID", userIDs));
    }

    /**
     * 填充 list 中的 user 字段
     *
     * @param oldList 旧 list
     * @return 填充后的 list
     */
    public final List<M> fillUserField(List<M> oldList) {
        var userIDs = oldList.stream().map(userinfo -> userinfo.userID).filter(Objects::nonNull).toArray();
        if (userIDs.length != 0) {
            var userIDUserMap = userService.list(new Query().in("id", userIDs)).stream().collect(Collectors.toMap((u) -> u.id, (u) -> u));
            return oldList.stream().peek(item -> item.user = userIDUserMap.get(item.userID)).collect(Collectors.toList());
        } else {
            return oldList;
        }
    }

}
