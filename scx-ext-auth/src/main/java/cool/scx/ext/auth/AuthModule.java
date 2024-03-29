package cool.scx.ext.auth;

import cool.scx.core.Scx;
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

    public static String BIND_WEBSOCKET_BY_TOKEN = "bind-websocket-by-token";

    /**
     * <p>initAuth.</p>
     */
    @SuppressWarnings("unchecked")
    public static void init(Scx scx) {
        var authHandler = (BaseAuthHandler<BaseUser>) scx.beanFactory().getBean(BaseAuthHandler.class);
        //绑定事件
        WSContext.wsConsumer(BIND_WEBSOCKET_BY_TOKEN, authHandler::bindWebSocketByToken);
        //设置处理器 ScxRoute 前置处理器
        scx.scxMvc().setInterceptor(new ApiPermsInterceptor(authHandler));
        //设置请求头
        scx.scxHttpRouter().corsHandler().allowedHeader(SCX_AUTH_TOKEN_KEY).allowedHeader(SCX_AUTH_DEVICE_KEY);
        //设置 cookie handler
        scx.scxHttpRouter().route().order(1).handler(new ScxAuthCookieHandler());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Scx scx) {
        init(scx);
        scx.beanFactory().getBean(BaseAuthHandler.class).readSessionFromFile();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop(Scx scx) {
        scx.beanFactory().getBean(BaseAuthHandler.class).writeSessionToFile();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "SCX_EXT-" + super.name();
    }

}
