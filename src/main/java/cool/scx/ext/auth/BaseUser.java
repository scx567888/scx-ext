package cool.scx.ext.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cool.scx.core.base.BaseModel;
import cool.scx.dao.annotation.Column;

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
    @Column(notNull = true, unique = true, needIndex = true)
    public String username;

    /**
     * 密码
     */
    @JsonIgnore
    public String password;

    /**
     * 是否为超级管理员
     */
    @Column(defaultValue = "false")
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

}
