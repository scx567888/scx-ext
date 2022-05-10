package cool.scx.test.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cool.scx.annotation.Column;
import cool.scx.annotation.ScxModel;
import cool.scx.base.BaseModel;
import cool.scx.ext.crud.annotation.UseCRUDApi;

/**
 * 核心用户类 (演示用,建议不要用于真实生产环境)
 *
 * @author scx567888
 * @version 1.1.2
 */
@UseCRUDApi
@ScxModel(tablePrefix = "test")
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
     * 是否为超级管理员
     */
    @Column(notNull = true, defaultValue = "false")
    @JsonIgnore
    public Boolean isAdmin;

}
