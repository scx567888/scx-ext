package cool.scx.ext.config_manager;

import cool.scx.core.base.BaseModel;
import cool.scx.data.jdbc.annotation.Column;

/**
 * <p>BaseSystemConfig class.</p>
 *
 * @author scx567888
 * @version 1.15.8
 */
public class BaseSystemConfig extends BaseModel {

    /**
     * 配置文件名称
     */
    @Column(unique = true)
    public String configName;

}
