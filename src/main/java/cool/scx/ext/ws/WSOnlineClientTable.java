package cool.scx.ext.ws;

import io.vertx.core.http.ServerWebSocket;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class WSOnlineClientTable {

    /**
     * 存储所有在线的 连接
     */
    private final Map<String, ServerWebSocket> onlineClients = new HashMap<>();

    public void add(ServerWebSocket... sockets) {
        for (var socket : sockets) {
            this.onlineClients.put(socket.binaryHandlerID(), socket);
        }
    }

    public ServerWebSocket remove(String webSocketID) {
        return onlineClients.remove(webSocketID);
    }

    public ServerWebSocket remove(ServerWebSocket webSocket) {
        return onlineClients.remove(webSocket.binaryHandlerID());
    }

    public ServerWebSocket get(String webSocketsID) {
        return onlineClients.get(webSocketsID);
    }

    public Collection<ServerWebSocket> onlineClients() {
        return onlineClients.values();
    }

    public int size() {
        return onlineClients.size();
    }

}
