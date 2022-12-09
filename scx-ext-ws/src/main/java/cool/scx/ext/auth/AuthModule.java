package cool.scx.ext.auth;

import cool.scx.core.ScxContext;
import cool.scx.core.ScxModule;
import cool.scx.ext.ws.WSContext;

import static cool.scx.ext.auth.BaseAuthHandler.SCX_AUTH_DEVICE_KEY;
import static cool.scx.ext.auth.BaseAuthHandler.SCX_AUTH_TOKEN_KEY;

/**
 * <p>OrganizationModule class.</p>
 *
 * @author scx567888
 * @version 1.11.8
 */
public class AuthModule extends ScxModule {

    /**
     * <p>initAuth.</p>
     */
    @SuppressWarnings("unchecked")
    public static void init() {
        var authHandler = (BaseAuthHandler<BaseUser>) ScxContext.getBean(BaseAuthHandler.class);
        //绑定事件
        WSContext.wsConsumer("bind-websocket-by-token", authHandler::bindWebSocketByToken);
        //设置处理器 ScxMapping 前置处理器
        ScxContext.scxMappingConfiguration().setScxMappingInterceptor(new ApiPermsInterceptor(authHandler));
        //设置请求头
        ScxContext.router().corsHandler().allowedHeader(SCX_AUTH_TOKEN_KEY).allowedHeader(SCX_AUTH_DEVICE_KEY);
        //设置 cookie handler
        ScxContext.router().vertxRouter().route().order(1).handler(new ScxAuthCookieHandler());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        init();
        ScxContext.getBean(BaseAuthHandler.class).readSessionFromFile();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        ScxContext.getBean(BaseAuthHandler.class).writeSessionToFile();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "SCX_EXT-" + super.name();
    }

}
