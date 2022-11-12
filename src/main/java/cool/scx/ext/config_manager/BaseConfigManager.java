package cool.scx.ext.config_manager;

import cool.scx.core.base.BaseModelService;
import cool.scx.sql.base.Query;
import cool.scx.sql.base.UpdateFilter;

public abstract class BaseConfigManager<S extends BaseSystemConfig, U extends BaseUserConfig> {

    /**
     * Constant <code>DEFAULT_CONFIG_NAME="ScxSystemConfig"</code>
     */
    public static final String DEFAULT_SYSTEM_CONFIG_NAME = "ScxSystemConfig";

    protected final BaseModelService<S> systemConfigService;
    protected final BaseModelService<U> userConfigService;

    public BaseConfigManager(BaseModelService<S> systemConfigService, BaseModelService<U> userConfigService) {
        this.systemConfigService = systemConfigService;
        this.userConfigService = userConfigService;
    }

    /**
     * 更新用户信息
     *
     * @param userID       用户 ID
     * @param newScxConfig 修改后的配置
     * @return a
     */
    public U updateUserConfig(Long userID, U newScxConfig) {
        long size = userConfigService.update(newScxConfig, new Query().equal("userID", userID), UpdateFilter.ofExcluded().addExcluded("userID"));
        if (size == 0) { //数据库中可能没有数据 也就是没更新成功
            newScxConfig.userID = userID;
            return userConfigService.add(newScxConfig);
        } else {
            return getUserConfig(userID);
        }
    }

    /**
     * 获取用户配置
     *
     * @param userID 用户 ID
     * @return 配置
     */
    public U getUserConfig(Long userID) {
        var config = userConfigService.get(new Query().equal("userID", userID));
        if (config == null) {
            var c = getDefaultUserConfig();
            c.userID = userID;
            config = userConfigService.add(c);
        }
        return config;
    }

    /**
     * <p>getSystemConfig.</p>
     *
     * @return s
     */
    public S getSystemConfig() {
        var config = systemConfigService.get(new Query().equal("configName", DEFAULT_SYSTEM_CONFIG_NAME));
        if (config == null) {
            var c = getDefaultSystemConfig();
            c.configName = DEFAULT_SYSTEM_CONFIG_NAME;
            config = systemConfigService.add(c);
        }
        return config;
    }

    /**
     * <p>updateSystemConfig.</p>
     *
     * @param newScxConfig a T object
     * @return a T object
     */
    public S updateSystemConfig(S newScxConfig) {
        long size = systemConfigService.update(newScxConfig, new Query().equal("configName", DEFAULT_SYSTEM_CONFIG_NAME), UpdateFilter.ofExcluded().addExcluded("configName"));
        if (size == 0) { //数据库中可能没有数据 也就是没更新成功
            newScxConfig.configName = DEFAULT_SYSTEM_CONFIG_NAME;
            return systemConfigService.add(newScxConfig);
        } else {
            return getSystemConfig();
        }
    }

    public abstract U getDefaultUserConfig();

    public abstract S getDefaultSystemConfig();

}
