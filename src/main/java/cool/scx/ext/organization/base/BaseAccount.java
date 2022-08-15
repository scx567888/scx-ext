package cool.scx.ext.organization.base;

import cool.scx.core.annotation.Column;

/**
 * 账号表 处理登录信息用
 *
 * @author scx567888
 * @version 1.11.8
 */
public abstract class BaseAccount<T extends BaseUser> extends UserInfoModel<T> {

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
