package cool.scx.ext.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import cool.scx.util.ObjectUtils;
import io.vertx.core.http.ServerWebSocket;

import java.util.HashMap;
import java.util.Map;

public class WSMessage<T> {

    private String address;
    private Map<String, Object> headers;
    private T body;
    private String replyAddress;

    private ServerWebSocket webSocket;

    public WSMessage() {

    }

    public WSMessage(String address) {
        this.address = address;
    }

    public WSMessage(String address, T body) {
        this.address = address;
        this.body = body;
    }

    public WSMessage(String address, T body, Map<String, Object> headers) {
        this.address = address;
        this.body = body;
        this.headers = new HashMap<>(headers);
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

    public static WSMessage<Object> fromJson(String json) throws JsonProcessingException {
        var jsonNode = ObjectUtils.jsonMapper().readValue(json, ObjectUtils.MAP_TYPE);
        var wsMessage = new WSMessage<>();
        wsMessage.address = ObjectUtils.convertValue(jsonNode.get("address"), String.class);
        wsMessage.body = jsonNode.get("body");
        wsMessage.headers = ObjectUtils.convertValue(jsonNode.get("headers"), ObjectUtils.MAP_TYPE);
        return wsMessage;
    }

    public String address() {
        return address;
    }

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
