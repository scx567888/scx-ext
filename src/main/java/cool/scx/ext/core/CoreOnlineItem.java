package cool.scx.ext.core;

import io.vertx.core.http.ServerWebSocket;

/**
 * websocket 在线对象
 * todo 尝试和 login item 整合为一个对象
 *
 * @author scx567888
 * @version 0.9.0
 */
public class CoreOnlineItem {

    /**
     * 连接
     */
    public final ServerWebSocket webSocket;

    /**
     * 此连接对应的用户名
     * 当初始连接的时候 username 会为空
     * 当登录成功时会通过websocket将认证成功的用户发送到服务的
     * 这时才会对 username 进行赋值
     */
    public Long userID;

    /**
     * OnlineItem 初始化函数
     * username 可以为空
     *
     * @param webSocket webSocket
     * @param userID    userID
     */
    public CoreOnlineItem(ServerWebSocket webSocket, Long userID) {
        this.webSocket = webSocket;
        this.userID = userID;
    }

    /**
     * <p>sendText.</p>
     *
     * @param str a {@link java.lang.String} object
     */
    public void send(String str) {
        webSocket.writeTextMessage(str);
    }

}
