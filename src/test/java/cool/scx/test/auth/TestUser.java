package cool.scx.test.auth;

import cool.scx.dao.annotation.Table;
import cool.scx.ext.auth.BaseUser;
import cool.scx.ext.crud.annotation.UseCRUDApi;

@UseCRUDApi(add = false, update = false)
@Table(tableName = "test_user")
public class TestUser extends BaseUser {

}
