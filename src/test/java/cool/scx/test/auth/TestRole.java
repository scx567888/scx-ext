package cool.scx.test.auth;

import cool.scx.core.annotation.ScxModel;
import cool.scx.ext.crud.annotation.UseCRUDApi;
import cool.scx.ext.organization.base.BaseRole;

/**
 * <p>Role class.</p>
 *
 * @author scx567888
 * @version 1.13.5
 */
@UseCRUDApi
@ScxModel(tableName = "test_role")
public final class TestRole extends BaseRole {

}
