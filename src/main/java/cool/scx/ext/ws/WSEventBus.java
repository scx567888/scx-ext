package cool.scx.ext.ws;

import cool.scx.core.Scx;
import cool.scx.core.eventbus.ZeroCopyMessageCodec;
import cool.scx.functional.ScxHandlerA;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.ServerWebSocket;

import java.util.Collection;

import static cool.scx.core.eventbus.ZeroCopyMessageCodec.ZERO_COPY_CODEC_NAME;

/**
 * 此 EventBus 仅支持 后台 和 前台进行通讯 并不会进行 jvm 内的通讯 如有需求 请使用 {@link Scx#eventBus()}
 */
public class WSEventBus {

    private final EventBus vertxEventBus = initVertxEventBus();

    private static EventBus initVertxEventBus() {
        return Vertx.vertx().eventBus().registerCodec(ZeroCopyMessageCodec.DEFAULT_INSTANCE);
    }

    public WSEventBus wsPublish(String address, Object body, Collection<ServerWebSocket> sockets) {
        return wsPublish(new WSMessage<>(address,body),sockets);
    }

    public WSEventBus wsPublish(WSMessage<?> wsMessage, Collection<ServerWebSocket> sockets) {
        var json = wsMessage.toJson();
        for (var allWebSocket : sockets) {
            allWebSocket.writeTextMessage(json);
        }
        return this;
    }

    public <T> WSEventBus wsConsumer(String address, ScxHandlerA<WSMessage<T>> handler) {
        vertxEventBus.consumer(address, (Message<WSMessage<T>> c) -> handler.handle(c.body()));
        return this;
    }

    void publishByWSMessage(WSMessage<?> wsMessage) {
        vertxEventBus.publish(wsMessage.address(), wsMessage, new DeliveryOptions().setCodecName(ZERO_COPY_CODEC_NAME));
    }

}
