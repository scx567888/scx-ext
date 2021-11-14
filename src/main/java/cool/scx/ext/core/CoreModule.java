package cool.scx.ext.core;

import cool.scx.ScxContext;
import cool.scx.ScxModule;

/**
 * 核心模块
 *
 * @author scx567888
 * @version 1.3.0
 */
public class CoreModule implements ScxModule {

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        ScxContext.eventBus().consumer("auth-token", CoreAuthLoginHandler::loginByWebSocket);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "SCX_EXT-" + ScxModule.super.name();
    }

}
