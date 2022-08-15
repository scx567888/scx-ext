package cool.scx.ext.organization._impl.user;

import cool.scx.core.annotation.ScxModel;
import cool.scx.ext.crud.annotation.UseCRUDApi;
import cool.scx.ext.organization.user.BaseUser;

@UseCRUDApi(add = false, update = false)
@ScxModel(tablePrefix = "organization")
public class User extends BaseUser {

}
