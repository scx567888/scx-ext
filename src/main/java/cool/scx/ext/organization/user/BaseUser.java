package cool.scx.ext.organization.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cool.scx.core.annotation.Column;
import cool.scx.core.annotation.NoColumn;
import cool.scx.core.base.BaseModel;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 核心用户类
 *
 * @author scx567888
 * @version 1.1.2
 */
public class BaseUser extends BaseModel {

    /**
     * 用户名 (注意 !!! ,用户名在业务上是可以被修改的 所以切记不要将用户名作为任何业务的关联字段)
     */
    @Column(notNull = true, unique = true)
    public String username;

    /**
     * 密码
     */
    @JsonIgnore
    public String password;

    /**
     * 是否为超级管理员
     */
    @Column(notNull = true, defaultValue = "false")
    public Boolean isAdmin;

    /**
     * 用户头像 id 此处存储的是 位于 uploadFile 表中的 id
     */
    public String avatar;

    /**
     * 电话号码
     */
    public String phoneNumber;

    /**
     * 邮箱地址
     */
    public String emailAddress;

    /**
     * 最后登录成功的状态 , 这里通过代码控制使其只存储最后10次
     */
    public List<LoginInfo> loginInfoHistory;

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
     * 登录信息 用来查询用户登录历史记录
     *
     * @param ip   ip
     * @param date date
     * @param type type
     */
    public record LoginInfo(String ip, LocalDateTime date, String type) {

    }

}
