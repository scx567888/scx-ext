package cool.scx.ext.config_manager;

import cool.scx.core.base.BaseModel;
import cool.scx.dao.annotation.Column;

/**
 * <p>BaseUserConfig class.</p>
 *
 * @author scx567888
 * @version 1.15.8
 */
public class BaseUserConfig extends BaseModel {

    @Column(unique = true)
    public Long userID;

}
