package cool.scx.test.auth;

import cool.scx.core.annotation.ScxModel;
import cool.scx.ext.auth.BaseUser;
import cool.scx.ext.crud.annotation.UseCRUDApi;

@UseCRUDApi(add = false, update = false)
@ScxModel(tableName = "test_user")
public class TestUser extends BaseUser {

}
