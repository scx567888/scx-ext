package cool.scx.ext.organization._impl;

import cool.scx.core.annotation.ScxModel;
import cool.scx.ext.crud.annotation.UseCRUDApi;
import cool.scx.ext.organization.base.BaseUser;

@UseCRUDApi(add = false, update = false)
@ScxModel(tablePrefix = "organization")
public class User extends BaseUser {

}
