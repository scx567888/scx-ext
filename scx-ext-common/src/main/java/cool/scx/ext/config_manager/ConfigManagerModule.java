package cool.scx.ext.config_manager;

import cool.scx.core.ScxModule;

/**
 * <p>ConfigManagerModule class.</p>
 *
 * @author scx567888
 * @version 1.15.8
 */
public class ConfigManagerModule extends ScxModule {
    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "SCX_EXT-" + super.name();
    }
}
