package cool.scx.test;

import cool.scx.ext.core.CoreOnlineItemHandler;
import cool.scx.ext.core.WSBody;

/**
 * 发送消息测试 handler
 *
 * @author scx567888
 * @version 1.1.17
 * @since 1.3.14
 */
public class SendMessageHandler {

    /**
     * 发送消息
     *
     * @param wsBody a {@link io.vertx.core.json.JsonObject} object
     */
    public static void sendMessage(WSBody wsBody) {
        //先获取消息
        var message = wsBody.data().asText().split("");
        System.out.println(wsBody.toJson());
        wsBody.webSocket().writeTextMessage("123123123123");
        System.out.println(wsBody.webSocket());
        //循环发送
        for (var aChar : message) {
            //向所有在线用户发送
            var onlineItemList = CoreOnlineItemHandler.getOnlineItemList();
            for (var onlineItem : onlineItemList) {
                onlineItem.send(new WSBody("writeMessage", aChar, null).toJson());
            }
        }
    }
}
