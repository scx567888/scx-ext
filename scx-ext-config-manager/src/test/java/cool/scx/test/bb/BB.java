package cool.scx.test.bb;

import cool.scx.core.annotation.ScxModel;
import cool.scx.core.base.BaseModel;

@ScxModel
public class BB extends BaseModel {
    public String name;
    public UU uu;
    public UU[] uus;

    public static class UU {
        public String[] phoneNumber;
        public String name;
    }

}