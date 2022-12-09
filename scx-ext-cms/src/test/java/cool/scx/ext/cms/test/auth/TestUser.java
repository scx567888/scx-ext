package cool.scx.ext.cms.test.auth;

import cool.scx.core.annotation.ScxModel;
import cool.scx.core.base.BaseModel;
import cool.scx.ext.crud.annotation.UseCRUDApi;

@UseCRUDApi(add = false, update = false)
@ScxModel(tableName = "test_user")
public class TestUser extends BaseModel {

    public String password;
    public String username;

    public String phoneNumber;
    public boolean isAdmin;

}
