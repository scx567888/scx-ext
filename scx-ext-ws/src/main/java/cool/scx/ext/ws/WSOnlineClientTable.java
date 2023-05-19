package cool.scx.ext.ws;

import io.vertx.core.http.ServerWebSocket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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
    private final Set<ServerWebSocket> onlineClients = new HashSet<>();

    public void add(ServerWebSocket sockets) {
        this.onlineClients.add(sockets);
    }

    public boolean remove(ServerWebSocket webSocket) {
        return onlineClients.remove(webSocket);
    }

    public Collection<ServerWebSocket> onlineClients() {
        return new ArrayList<>(onlineClients);
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
