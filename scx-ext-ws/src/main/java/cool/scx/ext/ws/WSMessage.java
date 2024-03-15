package cool.scx.ext.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import cool.scx.core.eventbus.ZeroCopyMessage;
import cool.scx.common.util.ObjectUtils;
import io.vertx.core.http.ServerWebSocket;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>WSMessage class.</p>
 *
 * @author scx567888
 * @version 1.15.0
 */
@ZeroCopyMessage
public class WSMessage<T> {

    private String address;
    private Map<String, Object> headers;
    private T body;
    private String replyAddress;

    private ServerWebSocket webSocket;

    /**
     * <p>Constructor for WSMessage.</p>
     */
    public WSMessage() {

    }

    /**
     * <p>Constructor for WSMessage.</p>
     *
     * @param address a {@link java.lang.String} object
     */
    public WSMessage(String address) {
        this.address = address;
    }

    /**
     * <p>Constructor for WSMessage.</p>
     *
     * @param address a {@link java.lang.String} object
     * @param body    a T object
     */
    public WSMessage(String address, T body) {
        this.address = address;
        this.body = body;
    }

    /**
     * <p>Constructor for WSMessage.</p>
     *
     * @param address a {@link java.lang.String} object
     * @param body    a T object
     * @param headers a {@link java.util.Map} object
     */
    public WSMessage(String address, T body, Map<String, Object> headers) {
        this.address = address;
        this.body = body;
        this.headers = new HashMap<>(headers);
    }

    public static WSMessage<Object> fromJson(String json) throws JsonProcessingException {
        var jsonNode = ObjectUtils.jsonMapper().readValue(json, ObjectUtils.MAP_TYPE);
        var wsMessage = new WSMessage<>();
        wsMessage.address = ObjectUtils.convertValue(jsonNode.get("address"), String.class);
        wsMessage.body = jsonNode.get("body");
        wsMessage.headers = ObjectUtils.convertValue(jsonNode.get("headers"), ObjectUtils.MAP_TYPE);
        return wsMessage;
    }

    /**
     * a
     *
     * @return a
     */
    public String toJson() {
        var map = new HashMap<>();
        map.put("address", this.address);
        map.put("body", this.body);
        map.put("headers", this.headers);
        return ObjectUtils.toJson(map, "");
    }

    /**
     * <p>address.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String address() {
        return address;
    }

    /**
     * <p>body.</p>
     *
     * @return a T object
     */
    public T body() {
        return body;
    }

    WSMessage<T> setWebSocket(ServerWebSocket webSocket) {
        this.webSocket = webSocket;
        return this;
    }

    public ServerWebSocket webSocket() {
        return this.webSocket;
    }

}
