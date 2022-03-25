package cool.scx.ext.core;

import com.google.common.collect.ArrayListMultimap;
import cool.scx.ScxContext;
import cool.scx.ScxHandler;
import cool.scx.ScxHandlerV;
import cool.scx.util.StringUtils;
import io.vertx.core.http.ServerWebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 处理注册及调用 websocket 事件
 *
 * @author scx567888
 * @version 1.6.2
 */
public final class WSParamHandlerRegister {

    /**
     * Constant <code>logger</code>
     */
    private static final Logger logger = LoggerFactory.getLogger(WSParamHandlerRegister.class);

    /**
     * 存储所有的 websocket 事件处理器 (方便前台调用) , key 为事件名 value 为事件
     */
    private static final ArrayListMultimap<String, ScxHandler<WSParam>> NAME_WS_PARAM_HANDLER_MAPPING = ArrayListMultimap.create();

    /**
     * 查找并执行
     *
     * @param wsBody    wsBody
     * @param webSocket web
     */
    static void findAndHandle(WSBody wsBody, ServerWebSocket webSocket) {
        //先获取名称
        if (StringUtils.isNotBlank(wsBody.name())) {
            var wsParam = new WSParam(wsBody.data(), webSocket);
            for (var wsParamHandler : NAME_WS_PARAM_HANDLER_MAPPING.get(wsBody.name())) {
                ScxContext.scheduler().submit(new WSParamHandlerWrapper(wsParamHandler, wsParam));
            }
        }
    }

    /**
     * 添加 handler
     *
     * @param name       名称
     * @param scxHandler handler (用于处理前台传过来的 websocket 连接)
     */
    public static void addHandler(String name, ScxHandler<WSParam> scxHandler) {
        NAME_WS_PARAM_HANDLER_MAPPING.put(name, scxHandler);
    }

    /**
     * 移除所有此名称的 handler
     *
     * @param name 名称
     */
    public static void removeAllHandler(String name) {
        NAME_WS_PARAM_HANDLER_MAPPING.removeAll(name);
    }

    /**
     * 移除 此 handler
     *
     * @param name       名称
     * @param scxHandler handler
     */
    public static void removeHandler(String name, ScxHandler<WSParam> scxHandler) {
        NAME_WS_PARAM_HANDLER_MAPPING.remove(name, scxHandler);
    }

    record WSParamHandlerWrapper(ScxHandler<WSParam> wsParamHandler, WSParam wsParam) implements ScxHandlerV {

        @Override
        public void handle() {
            try {
                wsParamHandler.handle(wsParam);
            } catch (Throwable throwable) {
                logger.error("执行 WSParamHandler 出错 !!!", throwable);
            }
        }

    }

}
