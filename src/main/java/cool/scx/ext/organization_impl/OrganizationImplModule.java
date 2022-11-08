package cool.scx.ext.organization_impl;

import cool.scx.core.ScxContext;
import cool.scx.core.ScxModule;
import cool.scx.ext.organization.auth.PermsAnnotationInterceptor;
import cool.scx.ext.organization.auth.ScxAuthCookieHandler;
import cool.scx.ext.organization_impl.impl.AuthHandler;
import cool.scx.ext.ws.WSContext;

import static cool.scx.ext.organization.base.BaseAuthHandler.SCX_AUTH_DEVICE_KEY;
import static cool.scx.ext.organization.base.BaseAuthHandler.SCX_AUTH_TOKEN_KEY;

public class OrganizationImplModule extends ScxModule {

    @Override
    public void start() {
        initAuth();
        ScxContext.getBean(AuthHandler.class).readSessionFromFile();
    }

    @Override
    public void stop() {
        ScxContext.getBean(AuthHandler.class).writeSessionToFile();
    }

    /**
     * 初始化 auth 模块
     */
    public void initAuth() {
        var authHandler = ScxContext.getBean(AuthHandler.class);
        AuthContext.setAuthHandler(authHandler);
        //绑定事件
        WSContext.wsConsumer("bind-websocket-by-token", authHandler::bindWebSocketByToken);
        //设置处理器 ScxMapping 前置处理器
        ScxContext.scxMappingConfiguration().setScxMappingInterceptor(new PermsAnnotationInterceptor(authHandler));
        //设置请求头
        ScxContext.router().corsHandler().allowedHeader(SCX_AUTH_TOKEN_KEY).allowedHeader(SCX_AUTH_DEVICE_KEY);
        //设置 cookie handler
        ScxContext.router().vertxRouter().route().order(1).handler(new ScxAuthCookieHandler());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "SCX_EXT-" + super.name();
    }

}
