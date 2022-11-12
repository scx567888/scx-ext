package cool.scx.ext.config_manager;

import cool.scx.core.annotation.Column;
import cool.scx.core.base.BaseModel;

public class BaseUserConfig extends BaseModel {

    @Column(unique = true)
    public Long userID;

}
