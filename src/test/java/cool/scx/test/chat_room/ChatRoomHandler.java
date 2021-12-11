package cool.scx.test.chat_room;

import cool.scx.ScxConstant;
import cool.scx.ScxContext;
import cool.scx.ext.core.CoreWebSocketHandler;
import cool.scx.ext.core.WSBody;

import java.time.LocalDateTime;

public class ChatRoomHandler {

    /**
     * 发送消息
     *
     * @param wsBody a {@link io.vertx.core.json.JsonObject} object
     */
    private static void sendMessage(WSBody wsBody) {
        //先获取消息
        var message = wsBody.data().asText();

        //向所有在线用户发送
        var onlineItemList = CoreWebSocketHandler.getAllWebSockets();
        for (var onlineItem : onlineItemList) {
            onlineItem.writeTextMessage(new WSBody("writeMessage", message, null).toJson());
        }
    }

    /**
     * 注册所有的 handler 请在模块 start 中调用
     */
    public static void registerAllHandler() {
        //注册事件
        ScxContext.eventBus().consumer("sendMessage", (m) -> sendMessage((WSBody) m));
        ScxContext.scheduleAtFixedRate(() -> {
            var onlineItemList = CoreWebSocketHandler.getAllWebSockets();
            for (var onlineItem : onlineItemList) {
                onlineItem.writeTextMessage(new WSBody("writeTime", ScxConstant.DEFAULT_DATETIME_FORMATTER.format(LocalDateTime.now()), null).toJson());
            }
        }, 0, 1000);
    }

}