package cool.scx.ext.config_manager;

import cool.scx.core.base.BaseModelService;
import cool.scx.data.Query;

import static cool.scx.data.ColumnFilter.ofExcluded;

/**
 * <p>Abstract BaseConfigManager class.</p>
 *
 * @author scx567888
 * @version 1.15.8
 */
public abstract class BaseConfigManager<S extends BaseSystemConfig, U extends BaseUserConfig> {

    /**
     * Constant <code>DEFAULT_CONFIG_NAME="ScxSystemConfig"</code>
     */
    public static final String DEFAULT_SYSTEM_CONFIG_NAME = "ScxSystemConfig";

    protected final BaseModelService<S> systemConfigService;
    protected final BaseModelService<U> userConfigService;

    /**
     * <p>Constructor for BaseConfigManager.</p>
     *
     * @param systemConfigService a {@link cool.scx.core.base.BaseModelService} object
     * @param userConfigService   a {@link cool.scx.core.base.BaseModelService} object
     */
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
        long size = userConfigService.update(newScxConfig, new Query().equal("userID", userID), ofExcluded().addExcluded("userID"));
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
        long size = systemConfigService.update(newScxConfig, new Query().equal("configName", DEFAULT_SYSTEM_CONFIG_NAME), ofExcluded().addExcluded("configName"));
        if (size == 0) { //数据库中可能没有数据 也就是没更新成功
            newScxConfig.configName = DEFAULT_SYSTEM_CONFIG_NAME;
            return systemConfigService.add(newScxConfig);
        } else {
            return getSystemConfig();
        }
    }

    /**
     * <p>getDefaultUserConfig.</p>
     *
     * @return a U object
     */
    public abstract U getDefaultUserConfig();

    /**
     * <p>getDefaultSystemConfig.</p>
     *
     * @return a S object
     */
    public abstract S getDefaultSystemConfig();

    /**
     * <p>Getter for the field <code>systemConfigService</code>.</p>
     *
     * @return a {@link cool.scx.core.base.BaseModelService} object
     */
    public final BaseModelService<S> getSystemConfigService() {
        return systemConfigService;
    }

    /**
     * <p>Getter for the field <code>userConfigService</code>.</p>
     *
     * @return a {@link cool.scx.core.base.BaseModelService} object
     */
    public final BaseModelService<U> getUserConfigService() {
        return userConfigService;
    }

}
