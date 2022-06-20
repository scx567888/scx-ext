package cool.scx.ext.organization.auth;

import cool.scx.ext.organization.user.User;

/**
 * 已经登录的客户端
 *
 * @author scx567888
 * @version 1.11.7
 */
public final class AlreadyLoginClient {

    /**
     * 唯一 ID 用于标识用户
     */
    public final User user;

    /**
     * 登陆的设备类型
     */
    public final DeviceType loginDevice;

    /**
     * 本质上一个是一个随机字符串
     * <p>
     * 前端 通过此值获取登录用户
     * <p>
     * 来源可以多种 header , cookie , url 等
     */
    public final String token;

    /**
     * 对应的 webSocketBinaryHandlerID
     */
    public String webSocketBinaryHandlerID;

    /**
     * a
     *
     * @param token       a
     * @param user        a
     * @param loginDevice a
     */
    public AlreadyLoginClient(String token, User user, DeviceType loginDevice) {
        this.token = token;
        this.user = user;
        this.loginDevice = loginDevice;
    }

}
