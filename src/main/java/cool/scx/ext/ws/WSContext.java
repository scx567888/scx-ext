package cool.scx.ext.ws;

import cool.scx.functional.ScxHandlerA;
import io.vertx.core.http.ServerWebSocket;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>WSContext class.</p>
 *
 * @author scx567888
 * @version 1.15.0
 */
public class WSContext {

    /**
     * 存储所有在线的 连接
     */
    private static final Map<String, ServerWebSocket> SERVER_WEB_SOCKETS = new HashMap<>();

    /**
     * Constant <code>wsEventBus</code>
     */
    private static final WSEventBus wsEventBus = new WSEventBus();

    /**
     * <p>wsEventBus.</p>
     *
     * @return a {@link cool.scx.ext.ws.WSEventBus} object
     */
    public static WSEventBus wsEventBus() {
        return wsEventBus;
    }

    /**
     * <p>wsConsumer.</p>
     *
     * @param address a {@link java.lang.String} object
     * @param handler a {@link cool.scx.functional.ScxHandlerA} object
     * @param <T>     a T class
     * @return a {@link cool.scx.ext.ws.WSEventBus} object
     */
    public static <T> WSEventBus wsConsumer(String address, ScxHandlerA<WSMessage<T>> handler) {
        return wsEventBus.wsConsumer(address, handler);
    }

    /**
     * <p>wsPublish.</p>
     *
     * @param wsMessage a {@link cool.scx.ext.ws.WSMessage} object
     * @param sockets   a {@link java.util.Collection} object
     * @return a {@link cool.scx.ext.ws.WSEventBus} object
     */
    public static WSEventBus wsPublish(WSMessage<?> wsMessage, Collection<ServerWebSocket> sockets) {
        return wsEventBus.wsPublish(wsMessage, sockets);
    }

    /**
     * <p>wsPublish.</p>
     *
     * @param address a {@link java.lang.String} object
     * @param body    a {@link java.lang.Object} object
     * @param sockets a {@link java.util.Collection} object
     * @return a {@link cool.scx.ext.ws.WSEventBus} object
     */
    public static WSEventBus wsPublish(String address, Object body, Collection<ServerWebSocket> sockets) {
        return wsEventBus.wsPublish(address, body, sockets);
    }

    /**
     * <p>wsPublishAll.</p>
     *
     * @param wsMessage a {@link cool.scx.ext.ws.WSMessage} object
     * @return a {@link cool.scx.ext.ws.WSEventBus} object
     */
    public static WSEventBus wsPublishAll(WSMessage<?> wsMessage) {
        return wsEventBus.wsPublish(wsMessage, WSContext.getAllWebSockets());
    }

    /**
     * <p>wsPublishAll.</p>
     *
     * @param address a {@link java.lang.String} object
     * @param body    a {@link java.lang.Object} object
     * @return a {@link cool.scx.ext.ws.WSEventBus} object
     */
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

    /**
     * <p>addServerWebSocket.</p>
     *
     * @param webSocket a {@link io.vertx.core.http.ServerWebSocket} object
     */
    static void addServerWebSocket(ServerWebSocket webSocket) {
        SERVER_WEB_SOCKETS.put(webSocket.binaryHandlerID(), webSocket);
    }

    /**
     * <p>removeServerWebSocket.</p>
     *
     * @param webSocket a {@link io.vertx.core.http.ServerWebSocket} object
     */
    static void removeServerWebSocket(ServerWebSocket webSocket) {
        SERVER_WEB_SOCKETS.remove(webSocket.binaryHandlerID());
    }

}
