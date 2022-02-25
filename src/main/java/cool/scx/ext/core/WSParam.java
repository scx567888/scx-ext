package cool.scx.ext.core;

import io.vertx.core.http.ServerWebSocket;

/**
 * a
 */
public record WSParam(Object data, ServerWebSocket webSocket) {

}