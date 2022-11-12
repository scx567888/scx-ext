package cool.scx.ext.config_manager;

import cool.scx.core.annotation.Column;
import cool.scx.core.base.BaseModel;

public class ScxUserConfig extends BaseModel {

    @Column(unique = true)
    public Long userID;

}
