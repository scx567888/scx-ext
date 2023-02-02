package cool.scx.test.bb;

import cool.scx.core.base.BaseModel;
import cool.scx.dao.annotation.Table;

@Table
public class BB extends BaseModel {
    public String name;
    public UU uu;
    public UU[] uus;

    public static class UU {
        public String[] phoneNumber;
        public String name;
    }

}