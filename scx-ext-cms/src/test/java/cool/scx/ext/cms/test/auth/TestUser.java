package cool.scx.ext.cms.test.auth;

import cool.scx.data.jdbc.annotation.Table;
import cool.scx.ext.auth.BaseUser;
import cool.scx.ext.crud.annotation.UseCRUDApi;

@UseCRUDApi(add = false, update = false)
@Table(tableName = "test_user")
public class TestUser extends BaseUser {

}
