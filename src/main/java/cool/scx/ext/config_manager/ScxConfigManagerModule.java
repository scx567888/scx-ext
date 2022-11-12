package cool.scx.ext.config_manager;

import cool.scx.core.ScxModule;

public class ScxConfigManagerModule extends ScxModule {
    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "SCX_EXT-" + super.name();
    }
}
