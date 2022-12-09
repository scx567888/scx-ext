package cool.scx.ext.config_manager.test.impl;

import cool.scx.core.annotation.ScxModel;
import cool.scx.ext.config_manager.BaseUserConfig;

@ScxModel(tablePrefix = "scx_test")
public class UserConfig extends BaseUserConfig {
    public String value1;
    public String value2;
    public String value3;
}
