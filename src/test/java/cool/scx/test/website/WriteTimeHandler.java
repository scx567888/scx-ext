package cool.scx.test.website;

import cool.scx.core.ScxConstant;
import cool.scx.core.ScxContext;
import cool.scx.ext.core.CoreWebSocketHandler;
import cool.scx.ext.core.WSBody;
import cool.scx.ext.core.WSParamHandlerRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;

public class WriteTimeHandler {

    private static final Logger logger = LoggerFactory.getLogger(WriteTimeHandler.class);

    /**
     * 注册所有的 handler 请在模块 start 中调用
     */
    public static void registerHandler() {
        //注册事件
        ScxContext.scheduler().scheduleAtFixedRate((status) -> {
            var onlineItemList = CoreWebSocketHandler.getAllWebSockets();
            for (var onlineItem : onlineItemList) {
                onlineItem.writeTextMessage(new WSBody("writeTime", ScxConstant.DEFAULT_DATETIME_FORMATTER.format(LocalDateTime.now())).toJson());
            }
        }, Duration.ofSeconds(1));
        //注册一个监控前台浏览器宽高变化的 handler
        WSParamHandlerRegister.addHandler("onResizeBrowser", (wsParam) -> {
            //这里添加一个延时模拟高负载任务处理 同时不会阻塞其他 handler
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //这里可以看到是在不同线程中运行的
            logger.error("有延迟 : " + wsParam.data());
        });
        WSParamHandlerRegister.addHandler("onResizeBrowser", (wsParam) -> {
            //测试
            logger.error("无延迟 : " + wsParam.data());
        });
    }

}