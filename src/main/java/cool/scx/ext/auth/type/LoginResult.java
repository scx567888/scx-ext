package cool.scx.ext.auth.type;

import cool.scx.ext.auth.BaseUser;

/**
 * 登录的结果
 *
 * @param token 签发的 token 可以用此 token 获取 session
 * @param user  对应的用户
 * @param <T>   用户类型
 */
public record LoginResult<T extends BaseUser>(String token, T user) {

}