package cool.scx.ext.ws;

import cool.scx.core.eventbus.ZeroCopyMessageCodec;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.ServerWebSocket;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * 此 EventBus 仅支持 后台 和 前台进行通讯 并不会进行 jvm 内的通讯 如有需求 请使用 {@link cool.scx.core.Scx#eventBus()}
 *
 * @author scx567888
 * @version 1.15.0
 */
public class WSEventBus {

    private final EventBus vertxEventBus = initVertxEventBus();

    private static EventBus initVertxEventBus() {
        var eventBus = Vertx.vertx().eventBus();
        ZeroCopyMessageCodec.registerCodec(eventBus);
        return eventBus;
    }

    /**
     * <p>wsPublish.</p>
     *
     * @param address a {@link java.lang.String} object
     * @param body    a {@link java.lang.Object} object
     * @param sockets a {@link java.util.Collection} object
     * @return a {@link cool.scx.ext.ws.WSEventBus} object
     */
    public WSEventBus wsPublish(String address, Object body, Collection<ServerWebSocket> sockets) {
        return wsPublish(new WSMessage<>(address, body), sockets);
    }

    /**
     * <p>wsPublish.</p>
     *
     * @param wsMessage a {@link cool.scx.ext.ws.WSMessage} object
     * @param sockets   a {@link java.util.Collection} object
     * @return a {@link cool.scx.ext.ws.WSEventBus} object
     */
    public WSEventBus wsPublish(WSMessage<?> wsMessage, Collection<ServerWebSocket> sockets) {
        var json = wsMessage.toJson();
        for (var socket : sockets) {
            if (socket != null && !socket.isClosed()) {
                socket.writeTextMessage(json);
            }
        }
        return this;
    }

    /**
     * <p>wsPublish.</p>
     *
     * @param address a {@link java.lang.String} object
     * @param body    a {@link java.lang.Object} object
     * @param sockets a {@link java.util.Collection} object
     * @return a {@link cool.scx.ext.ws.WSEventBus} object
     */
    public WSEventBus wsPublish(String address, Object body, ServerWebSocket... sockets) {
        return wsPublish(new WSMessage<>(address, body), sockets);
    }

    /**
     * <p>wsPublish.</p>
     *
     * @param wsMessage a {@link cool.scx.ext.ws.WSMessage} object
     * @param sockets   a {@link java.util.Collection} object
     * @return a {@link cool.scx.ext.ws.WSEventBus} object
     */
    public WSEventBus wsPublish(WSMessage<?> wsMessage, ServerWebSocket... sockets) {
        var json = wsMessage.toJson();
        for (var socket : sockets) {
            if (socket != null && !socket.isClosed()) {
                socket.writeTextMessage(json);
            }
        }
        return this;
    }

    /**
     * <p>wsConsumer.</p>
     *
     * @param address a {@link java.lang.String} object
     * @param handler a {@link Consumer} object
     * @param <T>     a T class
     * @return a {@link cool.scx.ext.ws.WSEventBus} object
     */
    public <T> WSEventBus wsConsumer(String address, Consumer<WSMessage<T>> handler) {
        vertxEventBus.consumer(address, (Message<WSMessage<T>> c) -> handler.accept(c.body()));
        return this;
    }

    /**
     * <p>publishByWSMessage.</p>
     *
     * @param wsMessage a {@link cool.scx.ext.ws.WSMessage} object
     */
    void publishByWSMessage(WSMessage<?> wsMessage) {
        vertxEventBus.publish(wsMessage.address(), wsMessage);
    }

}
