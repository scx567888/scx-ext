package cool.scx.test.chat_room;

import cool.scx.ScxConstant;
import cool.scx.ScxContext;
import cool.scx.ext.core.CoreWebSocketHandler;
import cool.scx.ext.core.WSBody;
import cool.scx.test.auth.AlreadyLoginClient;
import cool.scx.test.auth.TestAuth;
import cool.scx.util.ObjectUtils;
import io.vertx.core.http.ServerWebSocket;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class ChatRoomHandler {

    /**
     * 发送消息
     *
     * @param wsBody a {@link io.vertx.core.json.JsonObject} object
     */
    private static void sendMessage(WSBody wsBody) {
        //待接收的用户 id
        SendMessageBody sendMessageBody = new SendMessageBody();
        try {
            sendMessageBody = ObjectUtils.readValue(wsBody.data(), SendMessageBody.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Long userID : sendMessageBody.userIDs) {
            List<AlreadyLoginClient> alreadyLoginClients = TestAuth.alreadyLoginClients().stream().filter(c -> c.user().id.equals(userID)).toList();
            for (AlreadyLoginClient alreadyLoginClient : alreadyLoginClients) {
                ServerWebSocket webSocket = CoreWebSocketHandler.getWebSocket(alreadyLoginClient.webSocketBinaryHandlerID());
                webSocket.writeTextMessage(new WSBody("writeMessage", sendMessageBody.message, null).toJson());
            }
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

    public static class SendMessageBody {
        public List<Long> userIDs;
        public Object message;
    }

}