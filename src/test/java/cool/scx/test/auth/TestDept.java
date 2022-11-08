package cool.scx.test.auth;

import cool.scx.core.annotation.ScxModel;
import cool.scx.ext.crud.annotation.UseCRUDApi;
import cool.scx.ext.organization.base.BaseDept;

/**
 * <p>Dept class.</p>
 *
 * @author scx567888
 * @version 1.13.5
 */
@UseCRUDApi
@ScxModel(tablePrefix = "test_dept")
public final class TestDept extends BaseDept {

}
