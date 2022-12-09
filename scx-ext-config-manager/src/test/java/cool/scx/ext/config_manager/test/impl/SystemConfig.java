package cool.scx.ext.config_manager.test.impl;

import cool.scx.core.annotation.ScxModel;
import cool.scx.ext.config_manager.BaseSystemConfig;

@ScxModel(tablePrefix = "scx_test")
public class SystemConfig extends BaseSystemConfig {
    public String value1;
    public String value2;
    public String value3;
}
