package cool.scx.ext.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import cool.scx.mvc.annotation.ScxWebSocketRoute;
import cool.scx.mvc.base.BaseWebSocketHandler;
import cool.scx.mvc.websocket.OnCloseRoutingContext;
import cool.scx.mvc.websocket.OnExceptionRoutingContext;
import cool.scx.mvc.websocket.OnFrameRoutingContext;
import cool.scx.mvc.websocket.OnOpenRoutingContext;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cool.scx.ext.ws.WSContext.wsOnlineClientTable;

/**
 * 事件总线 websocket 连接处理类
 * <p>
 * 负责维护前台和后台的事件总线通讯
 *
 * @author scx567888
 * @version 1.0.16
 */
@ScxWebSocketRoute("/scx")
public class WSWebSocketHandler implements BaseWebSocketHandler {

    /**
     * 心跳检测字符
     */
    public static final String LOVE = "❤";

    /**
     * Constant <code>logger</code>
     */
    private static final Logger logger = LoggerFactory.getLogger(WSWebSocketHandler.class);

    /**
     * {@inheritDoc}
     * <p>
     * onOpen
     */
    @Override
    public void onOpen(OnOpenRoutingContext ctx) {
        wsOnlineClientTable().add(ctx.webSocket());
        logger.debug("{} 连接了!!! 当前总连接数 : {}", ctx.webSocket().remoteAddress(), wsOnlineClientTable().size());
        ctx.next();
    }

    /**
     * {@inheritDoc}
     * <p>
     * onClose
     */
    @Override
    public void onClose(OnCloseRoutingContext ctx) {
        //如果客户端终止连接 将此条连接作废
        wsOnlineClientTable().remove(ctx.webSocket());
        logger.debug("{} 关闭了!!! 当前总连接数 : {}", ctx.webSocket().remoteAddress(), wsOnlineClientTable().size());
        ctx.next();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onTextMessage( OnFrameRoutingContext ctx) throws JsonProcessingException {
        if (LOVE.equals(ctx.textData())) { //这里是心跳检测
            ctx.webSocket().writeTextMessage(LOVE);
        } else { //这里是事件
            WSContext.wsEventBus().publishByWSMessage(WSMessage.fromJson(ctx.textData()).setWebSocket(ctx.webSocket()));
        }
        ctx.next();
    }

    /**
     * 连接错误 打印错误 同时移除 连接
     * {@inheritDoc}
     */
    @Override
    public void onError(OnExceptionRoutingContext ctx) {
        wsOnlineClientTable().remove(ctx.webSocket());
        ctx.cause().printStackTrace();
        ctx.next();
    }

}
