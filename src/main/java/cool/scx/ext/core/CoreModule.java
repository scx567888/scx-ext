package cool.scx.ext.core;

import cool.scx.core.ScxModule;

/**
 * 核心模块
 *
 * @author scx567888
 * @version 1.3.0
 */
public class CoreModule extends ScxModule {

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "SCX_EXT-" + super.name();
    }

}
