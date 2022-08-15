package cool.scx.ext.organization.base.impl;

import cool.scx.core.annotation.ScxModel;
import cool.scx.ext.crud.annotation.UseCRUDApi;
import cool.scx.ext.organization.base.BaseUser;

/**
 * <p>User class.</p>
 *
 * @author scx567888
 * @version 1.13.5
 */
@UseCRUDApi(add = false, update = false)
@ScxModel(tablePrefix = "organization")
public final class User extends BaseUser {

}
