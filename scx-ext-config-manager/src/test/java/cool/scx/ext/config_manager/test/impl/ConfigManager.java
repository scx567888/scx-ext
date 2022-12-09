package cool.scx.ext.config_manager.test.impl;

import cool.scx.core.base.BaseModelService;
import cool.scx.ext.config_manager.BaseConfigManager;
import cool.scx.util.RandomUtils;
import org.springframework.stereotype.Component;

@Component
public class ConfigManager extends BaseConfigManager<SystemConfig, UserConfig> {

    /**
     * <p>Constructor for BaseConfigManager.</p>
     *
     * @param systemConfigService a {@link BaseModelService} object
     * @param userConfigService   a {@link BaseModelService} object
     */
    public ConfigManager(SystemConfigService systemConfigService, UserConfigService userConfigService) {
        super(systemConfigService, userConfigService);
    }

    @Override
    public UserConfig getDefaultUserConfig() {
        var u = new UserConfig();
        u.value1 = RandomUtils.randomString(10);
        u.value2 = RandomUtils.randomString(10);
        u.value3 = RandomUtils.randomString(10);
        return u;
    }

    @Override
    public SystemConfig getDefaultSystemConfig() {
        var s = new SystemConfig();
        s.value1 = RandomUtils.randomString(10);
        s.value2 = RandomUtils.randomString(10);
        s.value3 = RandomUtils.randomString(10);
        return s;
    }

}
