package cool.scx.ext.config_manager;

import cool.scx.enumeration.HttpMethod;
import cool.scx.ext.auth.BaseAuthHandler;
import cool.scx.ext.auth.annotation.ApiPerms;
import cool.scx.ext.ws.WSContext;
import cool.scx.mvc.annotation.ScxMapping;
import cool.scx.mvc.annotation.ScxWebSocketMapping;
import cool.scx.mvc.base.BaseWebSocketHandler;
import cool.scx.mvc.vo.Json;
import cool.scx.mvc.websocket.OnOpenRoutingContext;
import cool.scx.util.ObjectUtils;
import io.vertx.core.http.ServerWebSocket;

import java.util.Map;

/**
 * <p>ConfigManagerApi class.</p>
 *
 * @author scx567888
 * @version 1.15.8
 */
@ScxMapping("api")
@ScxWebSocketMapping(value = "/scx", order = 1)
public class ConfigManagerApi<S extends BaseSystemConfig, U extends BaseUserConfig> implements BaseWebSocketHandler {

    /**
     * 事件名称
     */
    public static final String ON_SCX_USER_CONFIG_CHANGE_EVENT_NAME = "onScxUserConfigChange";
    /**
     * a
     */
    public static final String ON_SCX_SYSTEM_CONFIG_CHANGE_EVENT_NAME = "onScxSystemConfigChange";
    protected final BaseConfigManager<S, U> configManager;
    protected final BaseAuthHandler<?> authHandler;
    private final Class<S> systemConfigClass;
    private final Class<U> userConfigClass;

    /**
     * <p>Constructor for ConfigManagerApi.</p>
     *
     * @param configManager a {@link cool.scx.ext.config_manager.BaseConfigManager} object
     * @param authHandler   a {@link cool.scx.ext.auth.BaseAuthHandler} object
     */
    public ConfigManagerApi(BaseConfigManager<S, U> configManager, BaseAuthHandler<?> authHandler) {
        this.configManager = configManager;
        this.authHandler = authHandler;
        this.systemConfigClass = configManager.getSystemConfigService()._baseDao()._entityClass();
        this.userConfigClass = configManager.getUserConfigService()._baseDao()._entityClass();
        initHandler();
    }

    /**
     * 更新系统配置
     *
     * @param config a
     * @return a {@link cool.scx.core.vo.Json} object
     */
    @ApiPerms
    @ScxMapping(value = "system-config", method = HttpMethod.PUT)
    public Json updateSystemConfig(Map<String, Object> config) {
        var systemConfig = ObjectUtils.convertValue(config, systemConfigClass);
        var newConfig = configManager.updateSystemConfig(systemConfig);
        WSContext.wsPublishAll(ON_SCX_SYSTEM_CONFIG_CHANGE_EVENT_NAME, newConfig);
        return Json.ok();
    }

    /**
     * <p>update.</p>
     *
     * @param config a a
     * @return a {@link cool.scx.core.vo.Json} object
     */
    @ApiPerms(checkPerms = false)
    @ScxMapping(value = "user-config", method = HttpMethod.PUT)
    public Json updateUserConfig(Map<String, Object> config) {
        var user = authHandler.getCurrentUser();
        var userConfig = ObjectUtils.convertValue(config, userConfigClass);
        var newConfig = configManager.updateUserConfig(user.id, userConfig);
        //获取当前登录用户的所有的在线连接客户端并发送事件
        var allWebSocket = authHandler.loggedInClientTable()
                .getByUserID(user.id).stream()
                .map(c -> WSContext.getOnlineClient(c.webSocketID))
                .toList();
        //广播事件
        WSContext.wsPublish(ON_SCX_USER_CONFIG_CHANGE_EVENT_NAME, newConfig, allWebSocket);
        return Json.ok();
    }

    /**
     * 初始化
     */
    public void initHandler() {
        WSContext.wsConsumer("bind-websocket-by-token", wsParam -> {
            var webSocket = wsParam.webSocket();
            var objectMap = ObjectUtils.convertValue(wsParam.body(), ObjectUtils.MAP_TYPE);
            //获取 token
            var token = ObjectUtils.convertValue(objectMap.get("token"), String.class);
            var client = authHandler.loggedInClientTable().getByToken(token);
            if (client != null) {
                WSContext.wsPublish(ON_SCX_USER_CONFIG_CHANGE_EVENT_NAME, configManager.getUserConfig(client.userID), webSocket);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onOpen(ServerWebSocket webSocket, OnOpenRoutingContext context) {
        //连接时我们广播事件
        WSContext.wsPublish(ON_SCX_SYSTEM_CONFIG_CHANGE_EVENT_NAME, configManager.getSystemConfig(), webSocket);
    }

}
