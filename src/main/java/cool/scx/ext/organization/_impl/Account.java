package cool.scx.ext.organization._impl;

import cool.scx.core.annotation.ScxModel;
import cool.scx.ext.crud.annotation.UseCRUDApi;
import cool.scx.ext.organization.base.BaseAccount;

/**
 * <p>Account class.</p>
 *
 * @author scx567888
 * @version 1.13.5
 */
@UseCRUDApi
@ScxModel(tablePrefix = "organization")
public final class Account extends BaseAccount<User> {

}
