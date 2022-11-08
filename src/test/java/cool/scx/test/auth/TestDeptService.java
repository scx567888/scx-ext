package cool.scx.test.auth;

import cool.scx.core.annotation.ScxService;
import cool.scx.ext.organization.base.BaseDeptService;
import cool.scx.ext.organization.base.UserDeptService;

/**
 * <p>DeptService class.</p>
 *
 * @author scx567888
 * @version 1.13.5
 */
@ScxService
public final class TestDeptService extends BaseDeptService<TestDept> {

    /**
     * <p>Constructor for DeptService.</p>
     *
     * @param userDeptService a {@link UserDeptService} object
     */
    public TestDeptService(UserDeptService userDeptService) {
        super(userDeptService);
    }

}
