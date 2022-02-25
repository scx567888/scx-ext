package cool.scx.ext.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import cool.scx.annotation.ScxWebSocketMapping;
import cool.scx.base.BaseWebSocketHandler;
import cool.scx.util.ObjectUtils;
import cool.scx.util.ansi.Ansi;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

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
     * 存储所有在线的 连接
     */
    private static final List<ServerWebSocket> SERVER_WEB_SOCKETS = new ArrayList<>();

    /**
     * 根据 binaryHandlerID 获取 ServerWebSocket
     *
     * @param binaryHandlerID a
     * @return a
     */
    public static ServerWebSocket getWebSocket(String binaryHandlerID) {
        return SERVER_WEB_SOCKETS.stream().filter(f -> f.binaryHandlerID().equals(binaryHandlerID)).findAny().orElse(null);
    }

    /**
     * 获取当前所有在线的连接对象
     *
     * @return 当前所有在线的连接对象
     */
    public static List<ServerWebSocket> getAllWebSockets() {
        return SERVER_WEB_SOCKETS;
    }

    /**
     * {@inheritDoc}
     * <p>
     * onOpen
     */
    @Override
    public void onOpen(ServerWebSocket webSocket) {
        SERVER_WEB_SOCKETS.add(webSocket);
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
        SERVER_WEB_SOCKETS.removeIf(f -> f.binaryHandlerID().equals(webSocket.binaryHandlerID()));
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
            var wsBody = ObjectUtils.jsonMapper().readValue(textData, WSBody.class);
            WSParamHandlerRegister.findAndHandle(wsBody, webSocket);
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
        SERVER_WEB_SOCKETS.removeIf(f -> f.binaryHandlerID().equals(webSocket.binaryHandlerID()));
        event.printStackTrace();
    }

}
