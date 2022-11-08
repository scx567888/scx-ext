package cool.scx.ext.organization;

import cool.scx.core.ScxContext;
import cool.scx.core.ScxModule;
import cool.scx.ext.organization.auth.PermsAnnotationInterceptor;
import cool.scx.ext.organization.auth.ScxAuthCookieHandler;
import cool.scx.ext.organization.base.BaseAuthHandler;
import cool.scx.ext.ws.WSContext;

import static cool.scx.ext.organization.base.BaseAuthHandler.SCX_AUTH_DEVICE_KEY;
import static cool.scx.ext.organization.base.BaseAuthHandler.SCX_AUTH_TOKEN_KEY;

/**
 * <p>OrganizationModule class.</p>
 *
 * @author scx567888
 * @version 1.11.8
 */
public class OrganizationModule extends ScxModule {

    public static void initAuth() {
        var authHandler = (BaseAuthHandler<?>) ScxContext.getBean(BaseAuthHandler.class);
        //绑定事件
        WSContext.wsConsumer("bind-websocket-by-token", authHandler::bindWebSocketByToken);
        //设置处理器 ScxMapping 前置处理器
        ScxContext.scxMappingConfiguration().setScxMappingInterceptor(new PermsAnnotationInterceptor(authHandler));
        //设置请求头
        ScxContext.router().corsHandler().allowedHeader(SCX_AUTH_TOKEN_KEY).allowedHeader(SCX_AUTH_DEVICE_KEY);
        //设置 cookie handler
        ScxContext.router().vertxRouter().route().order(1).handler(new ScxAuthCookieHandler());
    }

    @Override
    public void start() {
        initAuth();
        ScxContext.getBean(BaseAuthHandler.class).readSessionFromFile();
    }

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
