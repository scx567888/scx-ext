package cool.scx.test.website;

import cool.scx.ScxConstant;
import cool.scx.ScxContext;
import cool.scx.ext.core.CoreWebSocketHandler;
import cool.scx.ext.core.WSBody;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

public class WriteTimeHandler {

    /**
     * 注册所有的 handler 请在模块 start 中调用
     */
    public static void registerHandler() {
        //注册事件
        ScxContext.scheduler().scheduleAtFixedRate((status) -> {
            var onlineItemList = CoreWebSocketHandler.getAllWebSockets();
            for (var onlineItem : onlineItemList) {
                onlineItem.writeTextMessage(new WSBody("writeTime", ScxConstant.DEFAULT_DATETIME_FORMATTER.format(LocalDateTime.now()), null).toJson());
            }
        }, Instant.now(), Duration.ofSeconds(1));
    }

}