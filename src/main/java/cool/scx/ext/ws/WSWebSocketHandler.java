package cool.scx.ext.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import cool.scx.core.annotation.ScxWebSocketMapping;
import cool.scx.core.base.BaseWebSocketHandler;
import cool.scx.util.ansi.Ansi;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cool.scx.ext.ws.WSContext.*;

/**
 * 事件总线 websocket 连接处理类
 * <p>
 * 负责维护前台和后台的事件总线通讯
 *
 * @author scx567888
 * @version 1.0.16
 */
@ScxWebSocketMapping("/scx")
public class WSWebSocketHandler implements BaseWebSocketHandler {

    /**
     * Constant <code>logger</code>
     */
    private static final Logger logger = LoggerFactory.getLogger(WSWebSocketHandler.class);

    /**
     * 心跳检测字符
     */
    public static final String LOVE = "❤";

    /**
     * {@inheritDoc}
     * <p>
     * onOpen
     */
    @Override
    public void onOpen(ServerWebSocket webSocket) {
        addServerWebSocket(webSocket);
        logger.debug("{} 连接了!!! 当前总连接数 : {}", webSocket.binaryHandlerID(), getAllWebSockets().size());
    }

    /**
     * {@inheritDoc}
     * <p>
     * onClose
     */
    @Override
    public void onClose(ServerWebSocket webSocket) {
        //如果客户端终止连接 将此条连接作废
        removeServerWebSocket(webSocket);
        logger.debug("{} 关闭了!!! 当前总连接数 : {}", webSocket.binaryHandlerID(), getAllWebSockets().size());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onTextMessage(String textData, WebSocketFrame h, ServerWebSocket webSocket) throws JsonProcessingException {
        if (LOVE.equals(textData)) { //这里是心跳检测
            webSocket.writeTextMessage(LOVE);
        } else { //这里是事件
            WSContext.wsEventBus().publishByWSMessage(WSMessage.fromJson(textData).setWebSocket(webSocket));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBinaryMessage(Buffer binaryData, WebSocketFrame h, ServerWebSocket webSocket) {
        Ansi.out().color("onBinaryMessage").println();
    }

    /**
     * 连接错误 打印错误 同时移除 连接
     * {@inheritDoc}
     */
    @Override
    public void onError(Throwable event, ServerWebSocket webSocket) {
        removeServerWebSocket(webSocket);
        event.printStackTrace();
    }

}
