package cool.scx.ext.organization.account;

import cool.scx.core.annotation.Column;
import cool.scx.core.annotation.ScxModel;
import cool.scx.ext.crud.annotation.UseCRUDApi;
import cool.scx.ext.organization.auth.UserInfoModel;

/**
 * 账号表 处理登录信息用
 *
 * @author scx567888
 * @version 1.11.8
 */
@UseCRUDApi
@ScxModel(tablePrefix = "organization")
public class Account extends UserInfoModel {

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
