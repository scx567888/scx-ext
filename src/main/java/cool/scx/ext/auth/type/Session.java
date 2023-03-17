package cool.scx.ext.auth.type;

import io.vertx.core.http.ServerWebSocket;

/**
 * 已经登录的客户端
 *
 * @author scx567888
 * @version 1.11.7
 */
public final class Session {

    /**
     * 唯一 ID 用于标识用户
     */
    public Long userID;

    /**
     * 登陆的设备类型
     */
    public DeviceType loginDevice;

    /**
     * 本质上一个是一个随机字符串
     * <p>
     * 前端 通过此值获取登录用户
     * <p>
     * 来源可以多种 header , cookie , url 等
     */
    public String token;

    /**
     * 对应的 webSocket
     */
    public ServerWebSocket webSocket;

    /**
     * <p>Constructor for LoggedInClient.</p>
     */
    public Session() {

    }

    /**
     * <p>Constructor for LoggedInClient.</p>
     *
     * @param token       a {@link java.lang.String} object
     * @param userID      a {@link java.lang.Long} object
     * @param loginDevice a {@link DeviceType} object
     */
    public Session(String token, Long userID, DeviceType loginDevice) {
        this.userID = userID;
        this.loginDevice = loginDevice;
        this.token = token;
    }

}
