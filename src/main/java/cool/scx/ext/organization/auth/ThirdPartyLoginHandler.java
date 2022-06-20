package cool.scx.ext.organization.auth;

import cool.scx.ext.organization.account.Account;
import cool.scx.ext.organization.user.User;

/**
 * <p>ThirdPartyLoginHandler interface.</p>
 *
 * @author scx567888
 * @version 1.11.8
 */
public interface ThirdPartyLoginHandler {

    /**
     * <p>tryLogin.</p>
     *
     * @param uniqueID    a {@link java.lang.String} object
     * @param accessToken a {@link java.lang.String} object
     * @return a {@link cool.scx.ext.organization.user.User} object
     */
    User tryLogin(String uniqueID, String accessToken);

    /**
     * 以密码举例 这就是修改密码
     *
     * @param uniqueID       用户名
     * @param newAccessToken 新密码
     * @param oldAccessToken 旧密码
     * @return 更新后的账号
     */
    Account changeAccessToken(String uniqueID, String newAccessToken, String oldAccessToken);

    /**
     * 以密码距离 这就是修改用户名
     *
     * @param accessToken 密码
     * @param newUniqueID 新用户名
     * @param oldUniqueID 旧用户名
     * @return 更新后的账号
     */
    Account changeUniqueID(String accessToken, String newUniqueID, String oldUniqueID);

    /**
     * 根据用户 ID 获取账号
     *
     * @param userID userID
     * @return r
     */
    Account getByUserID(Long userID);


    /**
     * <p>getByUniqueID.</p>
     *
     * @param uniqueID a {@link java.lang.String} object
     * @return a {@link cool.scx.ext.organization.account.Account} object
     */
    Account getByUniqueID(String uniqueID);


    /**
     * <p>signup.</p>
     *
     * @param uniqueID    a {@link java.lang.String} object
     * @param accessToken a {@link java.lang.String} object
     * @param defaultUser a {@link cool.scx.ext.organization.user.User} object
     * @return a {@link cool.scx.ext.organization.user.User} object
     */
    User signup(String uniqueID, String accessToken, User defaultUser);

}
