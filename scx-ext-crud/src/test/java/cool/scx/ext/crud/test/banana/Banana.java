package cool.scx.ext.crud.test.banana;

import cool.scx.core.base.BaseModel;
import cool.scx.data.jdbc.annotation.Table;
import cool.scx.ext.crud.annotation.UseCRUDApi;

@UseCRUDApi
@Table
public class Banana extends BaseModel {

    public String name;

}
