package cool.scx.ext.organization;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cool.scx.annotation.Column;
import cool.scx.annotation.NoColumn;
import cool.scx.annotation.ScxModel;
import cool.scx.base.BaseModel;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 核心用户类 (演示用,建议不要用于真实生产环境)
 *
 * @author scx567888
 * @version 1.1.2
 */
@ScxModel(tablePrefix = "organization")
public class User extends BaseModel {

    /**
     * 登录名
     */
    @Column(notNull = true, unique = true)
    public String username;

    /**
     * 已加密的登录密码
     */
    @Column(notNull = true)
    @JsonIgnore
    public String password;

    /**
     * 昵称
     */
    public String nickname;

    /**
     * 用户头像 id 此处存储的是 位于 uploadFile 表中的 id
     */
    public String avatar;

    /**
     * 最后登录成功的时间 , 这里通过代码控制使其只存储最后10次
     */
    @JsonIgnore
    public List<LocalDateTime> lastLoginDateList;

    /**
     * 最后登录成功的IP , 这里通过代码控制使其只存储最后10次
     */
    @JsonIgnore
    public List<String> lastLoginIPList;

    /**
     * 是否为超级管理员
     */
    @Column(notNull = true, defaultValue = "false")
    @JsonIgnore
    public Boolean isAdmin;

    /**
     * dept id 集合
     */
    @NoColumn
    @JsonIgnore
    public List<Long> deptIDs;

    /**
     * role id 集合
     */
    @NoColumn
    @JsonIgnore
    public List<Long> roleIDs;

}
