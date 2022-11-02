package cool.scx.ext.ws;

import io.vertx.core.http.ServerWebSocket;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>WSOnlineClientTable class.</p>
 *
 * @author scx567888
 * @version 1.15.0
 */
public class WSOnlineClientTable {

    /**
     * 存储所有在线的 连接
     */
    private final Map<String, ServerWebSocket> onlineClients = new HashMap<>();

    /**
     * <p>add.</p>
     *
     * @param sockets a {@link io.vertx.core.http.ServerWebSocket} object
     */
    public void add(ServerWebSocket... sockets) {
        for (var socket : sockets) {
            this.onlineClients.put(socket.binaryHandlerID(), socket);
        }
    }

    /**
     * <p>remove.</p>
     *
     * @param webSocketID a {@link java.lang.String} object
     * @return a {@link io.vertx.core.http.ServerWebSocket} object
     */
    public ServerWebSocket remove(String webSocketID) {
        return onlineClients.remove(webSocketID);
    }

    /**
     * <p>remove.</p>
     *
     * @param webSocket a {@link io.vertx.core.http.ServerWebSocket} object
     * @return a {@link io.vertx.core.http.ServerWebSocket} object
     */
    public ServerWebSocket remove(ServerWebSocket webSocket) {
        return onlineClients.remove(webSocket.binaryHandlerID());
    }

    /**
     * <p>get.</p>
     *
     * @param webSocketsID a {@link java.lang.String} object
     * @return a {@link io.vertx.core.http.ServerWebSocket} object
     */
    public ServerWebSocket get(String webSocketsID) {
        return onlineClients.get(webSocketsID);
    }

    /**
     * <p>onlineClients.</p>
     *
     * @return a {@link java.util.Collection} object
     */
    public Collection<ServerWebSocket> onlineClients() {
        return onlineClients.values();
    }

    /**
     * <p>size.</p>
     *
     * @return a int
     */
    public int size() {
        return onlineClients.size();
    }

}
