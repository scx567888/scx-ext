package cool.scx.ext.core;

import cool.scx.ScxContext;
import cool.scx.annotation.ScxWebSocketMapping;
import cool.scx.base.BaseWebSocketHandler;
import cool.scx.util.ObjectUtils;
import cool.scx.util.ansi.Ansi;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 时间总线 websocket 连接处理类
 * <p>
 * 负责维护前台和后台的事件总线通讯
 *
 * @author scx567888
 * @version 1.0.16
 */
@ScxWebSocketMapping("/scx")
public class CoreWebSocketHandler implements BaseWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(CoreWebSocketHandler.class);

    /**
     * 心跳检测字符
     */
    private static final String LOVE = "❤";

    /**
     * 根据 前台发送的字符串封装实体
     *
     * @param text      text
     * @param webSocket w
     * @return w
     */
    private static WSBody createScxWebSocketEvent(String text, ServerWebSocket webSocket) {
        try {
            var jsonNode = ObjectUtils.mapper().readTree(text);
            var eventName = jsonNode.get("eventName").asText();
            return new WSBody(eventName, jsonNode.get("data"), webSocket);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * onOpen
     */
    @Override
    public void onOpen(ServerWebSocket webSocket) {
        CoreOnlineItemHandler.addOnlineItem(webSocket, null);
        logger.debug("{} 连接了!!! 当前总连接数 : {}", webSocket.binaryHandlerID(), CoreOnlineItemHandler.getOnlineItemList().size());
    }

    /**
     * {@inheritDoc}
     * <p>
     * onClose
     */
    @Override
    public void onClose(ServerWebSocket webSocket) {
        //如果客户端终止连接 将此条连接作废
        CoreOnlineItemHandler.removeOnlineItemByWebSocket(webSocket);
        logger.debug("{} 关闭了!!! 当前总连接数 : {}", webSocket.binaryHandlerID(), CoreOnlineItemHandler.getOnlineItemList().size());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onTextMessage(String textData, WebSocketFrame h, ServerWebSocket webSocket) {
        //这里是心跳检测
        if (LOVE.equals(textData)) {
            webSocket.writeTextMessage(LOVE);
        } else { //这里是其他事件
            var event = createScxWebSocketEvent(textData, webSocket);
            if (event != null) {
                ScxContext.eventBus().publish(event.eventName(), event);
            }
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
     * {@inheritDoc}
     */
    @Override
    public void onError(Throwable event, ServerWebSocket webSocket) {
        event.printStackTrace();
    }

}
