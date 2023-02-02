package cool.scx.ext.auth;

import cool.scx.core.base.BaseModel;
import cool.scx.dao.annotation.Column;

/**
 * 账号表 处理登录信息用
 *
 * @author scx567888
 * @version 1.11.8
 */
public class BaseAccount extends BaseModel {

    /**
     * 唯一ID
     */
    @Column(notNull = true)
    public String uniqueID;

    /**
     * 认证 TOKEN
     */
    @Column(notNull = true)
    public String accessToken;

    /**
     * 账号类型
     */
    @Column(notNull = true)
    public String accountType;

}
