package cool.scx.ext.organization.base;

import cool.scx.core.base.BaseModelService;
import cool.scx.core.base.Query;
import cool.scx.core.base.SelectFilter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>Abstract UserInfoModelService class.</p>
 *
 * @author scx567888
 * @version 1.11.8
 * todo 这里的泛型设计有问题
 */
public abstract class UserInfoModelService<M extends UserInfoModel<?>> extends BaseModelService<M> {

    /**
     * userService
     */
    protected final BaseUserService<?> userService;

    /**
     * <p>Constructor for UserInfoModelService.</p>
     *
     * @param userService a {@link cool.scx.ext.organization.base.BaseUserService} object
     */
    protected UserInfoModelService(BaseUserService<?> userService) {
        this.userService = userService;
    }

    /**
     * {@inheritDoc}
     * <p>
     * 重写 list 方法以使用 userID 填充 user 对象
     */
    @Override
    public List<M> list(Query query, SelectFilter selectFilter) {
        return fillUserField(super.list(query, selectFilter), query);
    }

    /**
     * 根据用户查询 信息
     *
     * @param user user
     * @return list
     */
    public final M getByUser(BaseUser user) {
        if (user != null) {
            return get(new Query().equal("userID", user.id));
        }
        return null;
    }

    /**
     * <p>getByUserWithoutUserField.</p>
     *
     * @param user a {@link cool.scx.ext.organization.base.BaseUser} object
     * @return a M object
     */
    public final M getByUserWithoutUserField(BaseUser user) {
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
    public final List<M> listByUser(List<BaseUser> userList) {
        var userIDs = userList.stream().map(user -> user.id).toArray();
        return list(new Query().in("userID", userIDs));
    }

    /**
     * 填充 list 中的 user 字段
     *
     * @param oldList 旧 list
     * @param query   a
     * @return 填充后的 list
     */
    public final List<M> fillUserField(List<M> oldList, Query query) {
        var userIDUserMap = userService.list(new Query().in("id", buildListSQLWithAlias(query, SelectFilter.ofIncluded("userID")))).stream().collect(Collectors.toMap(u -> u.id, u -> u));
        return oldList.stream().peek(item -> item.user = userIDUserMap.get(item.userID)).collect(Collectors.toList());
    }

}
