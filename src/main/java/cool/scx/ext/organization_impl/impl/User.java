package cool.scx.ext.organization_impl.impl;

import cool.scx.core.annotation.NoColumn;
import cool.scx.core.annotation.ScxModel;
import cool.scx.ext.crud.annotation.UseCRUDApi;
import cool.scx.ext.organization.base.BaseUser;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>User class.</p>
 *
 * @author scx567888
 * @version 1.13.5
 */
@UseCRUDApi(add = false, update = false)
@ScxModel(tablePrefix = "organization")
public final class User extends BaseUser {

    /**
     * dept id 集合
     */
    @NoColumn
    public List<Long> deptIDs;

    /**
     * role id 集合
     */
    @NoColumn
    public List<Long> roleIDs;

    /**
     * 最后登录成功的状态 , 这里通过代码控制使其只存储最后10次
     */
    public List<LoginInfo> loginInfoHistory;


    /**
     * 登录信息 用来查询用户登录历史记录
     *
     * @param ip   ip
     * @param date date
     * @param type type
     */
    public record LoginInfo(String ip, LocalDateTime date, String type) {

    }

}
