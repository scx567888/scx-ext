package cool.scx.test.auth;

import cool.scx.core.annotation.ScxModel;
import cool.scx.ext.crud.annotation.UseCRUDApi;
import cool.scx.ext.organization.base.BaseUser;

@UseCRUDApi(add = false, update = false)
@ScxModel(tableName = "test_user")
public class TestUser extends BaseUser {

}
