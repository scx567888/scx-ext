package cool.scx.ext.organization.auth;

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
     * 对应的 webSocketBinaryHandlerID
     */
    public String webSocketBinaryHandlerID;

}
