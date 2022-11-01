package cool.scx.ext.ws;

import cool.scx.functional.ScxHandlerA;
import io.vertx.core.http.ServerWebSocket;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class WSContext {

    /**
     * 存储所有在线的 连接
     */
    private static final Map<String, ServerWebSocket> SERVER_WEB_SOCKETS = new HashMap<>();

    private static final WSEventBus wsEventBus = new WSEventBus();

    public static WSEventBus wsEventBus() {
        return wsEventBus;
    }

    public static <T> WSEventBus wsConsumer(String address, ScxHandlerA<WSMessage<T>> handler) {
        return wsEventBus.wsConsumer(address, handler);
    }

    public static WSEventBus wsPublish(WSMessage<?> wsMessage, Collection<ServerWebSocket> sockets) {
        return wsEventBus.wsPublish(wsMessage, sockets);
    }

    public static WSEventBus wsPublish(String address, Object body, Collection<ServerWebSocket> sockets) {
        return wsEventBus.wsPublish(address, body, sockets);
    }

    public static WSEventBus wsPublishAll(WSMessage<?> wsMessage) {
        return wsEventBus.wsPublish(wsMessage, WSContext.getAllWebSockets());
    }

    public static WSEventBus wsPublishAll(String address, Object body) {
        return wsEventBus.wsPublish(address, body, WSContext.getAllWebSockets());
    }

    /**
     * 根据 binaryHandlerID 获取 ServerWebSocket
     *
     * @param binaryHandlerID a
     * @return a
     */
    public static ServerWebSocket getWebSocket(String binaryHandlerID) {
        return SERVER_WEB_SOCKETS.get(binaryHandlerID);
    }

    /**
     * 获取当前所有在线的连接对象
     *
     * @return 当前所有在线的连接对象
     */
    public static Collection<ServerWebSocket> getAllWebSockets() {
        return SERVER_WEB_SOCKETS.values();
    }

    static void addServerWebSocket(ServerWebSocket webSocket) {
        SERVER_WEB_SOCKETS.put(webSocket.binaryHandlerID(), webSocket);
    }

    static void removeServerWebSocket(ServerWebSocket webSocket) {
        SERVER_WEB_SOCKETS.remove(webSocket.binaryHandlerID());
    }

}
