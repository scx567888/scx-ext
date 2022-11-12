package cool.scx.ext.config_manager;

import cool.scx.core.annotation.Column;
import cool.scx.core.base.BaseModel;

public class ScxSystemConfig extends BaseModel {

    /**
     * 配置文件名称
     */
    @Column(unique = true)
    public String configName;

}
