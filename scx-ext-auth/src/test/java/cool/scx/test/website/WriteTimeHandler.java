package cool.scx.test.website;

import cool.scx.core.ScxConstant;
import cool.scx.core.ScxContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;

import static cool.scx.ext.ws.WSContext.wsConsumer;
import static cool.scx.ext.ws.WSContext.wsPublishAll;

public class WriteTimeHandler {

    private static final Logger logger = LoggerFactory.getLogger(WriteTimeHandler.class);

    /**
     * 注册所有的 handler 请在模块 start 中调用
     */
    public static void registerHandler() {
        //注册事件
        ScxContext.scheduler().scheduleAtFixedRate((status) -> {
            var nowTime = ScxConstant.DEFAULT_DATETIME_FORMATTER.format(LocalDateTime.now());
            wsPublishAll("writeTime", nowTime);
        }, Duration.ofSeconds(1));
        //注册一个监控前台浏览器宽高变化的 handler
        wsConsumer("onResizeBrowser", (wsParam) -> {
            //测试
            logger.error("浏览器大小 : " + wsParam.body());
        });
    }

}