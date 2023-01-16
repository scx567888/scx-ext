package cool.scx.ext.ws;

import io.vertx.core.http.ServerWebSocket;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * <p>WSContext class.</p>
 *
 * @author scx567888
 * @version 1.15.0
 */
public class WSContext {

    /**
     * Constant <code>wsOnlineClientTable</code>
     */
    private static final WSOnlineClientTable wsOnlineClientTable = new WSOnlineClientTable();

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
     * @param handler a {@link Consumer} object
     * @param <T>     a T class
     * @return a {@link cool.scx.ext.ws.WSEventBus} object
     */
    public static <T> WSEventBus wsConsumer(String address, Consumer<WSMessage<T>> handler) {
        return wsEventBus.wsConsumer(address, handler);
    }

    /**
     * <p>wsPublish.</p>
     *
     * @param wsMessage a {@link cool.scx.ext.ws.WSMessage} object
     * @param sockets   a {@link io.vertx.core.http.ServerWebSocket} object
     * @return a {@link cool.scx.ext.ws.WSEventBus} object
     */
    public static WSEventBus wsPublish(WSMessage<?> wsMessage, ServerWebSocket... sockets) {
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
    public static WSEventBus wsPublish(String address, Object body, ServerWebSocket... sockets) {
        return wsEventBus.wsPublish(address, body, sockets);
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
        return wsEventBus.wsPublish(wsMessage, onlineClients());
    }

    /**
     * <p>wsPublishAll.</p>
     *
     * @param address a {@link java.lang.String} object
     * @param body    a {@link java.lang.Object} object
     * @return a {@link cool.scx.ext.ws.WSEventBus} object
     */
    public static WSEventBus wsPublishAll(String address, Object body) {
        return wsEventBus.wsPublish(address, body, onlineClients());
    }

    /**
     * 根据 binaryHandlerID 获取 ServerWebSocket
     *
     * @param webSocketsID a
     * @return a
     */
    public static ServerWebSocket getOnlineClient(String webSocketsID) {
        return wsOnlineClientTable.get(webSocketsID);
    }

    /**
     * 获取当前所有在线的连接对象
     *
     * @return 当前所有在线的连接对象
     */
    public static Collection<ServerWebSocket> onlineClients() {
        return wsOnlineClientTable.onlineClients();
    }

    /**
     * <p>wsOnlineClientTable.</p>
     *
     * @return a {@link cool.scx.ext.ws.WSOnlineClientTable} object
     */
    static WSOnlineClientTable wsOnlineClientTable() {
        return wsOnlineClientTable;
    }

}
