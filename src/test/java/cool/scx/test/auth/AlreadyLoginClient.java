package cool.scx.test.auth;

import cool.scx.test.user.User;

import java.io.Serializable;

/**
 * 已经登录的客户端
 */
public final class AlreadyLoginClient implements Serializable {

    /**
     * 唯一 ID 用于标识用户
     */
    private final User user;

    /**
     * 登陆的设备类型
     */
    private final DeviceType loginDevice;
    /**
     * 本质上一个是一个随机字符串
     * <p>
     * 前端 通过此值获取登录用户
     * <p>
     * 来源可以多种 header , cookie , url 等
     */
    private final String token;
    /**
     * 对应的 webSocketBinaryHandlerID
     */
    private String webSocketBinaryHandlerID;

    /**
     * 构造函数
     *
     * @param loginDevice {@link #loginDevice}
     * @param token       {@link #token}
     * @param user        {@link #user}
     */
    public AlreadyLoginClient(String token, User user, DeviceType loginDevice) {
        this.token = token;
        this.user = user;
        this.loginDevice = loginDevice;
    }

    public String webSocketBinaryHandlerID() {
        return webSocketBinaryHandlerID;
    }

    public void webSocketBinaryHandlerID(String webSocketBinaryHandlerID) {
        this.webSocketBinaryHandlerID = webSocketBinaryHandlerID;
    }

    public User user() {
        return user;
    }

    public DeviceType loginDevice() {
        return loginDevice;
    }

    public String token() {
        return token;
    }

}