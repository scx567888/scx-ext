package cool.scx.test.auth;

import cool.scx.core.annotation.ScxService;
import cool.scx.ext.organization.base.BaseRoleService;
import cool.scx.ext.organization.base.UserRoleService;

/**
 * <p>RoleService class.</p>
 *
 * @author scx567888
 * @version 1.13.5
 */
@ScxService
public final class TestRoleService extends BaseRoleService<TestRole> {

    /**
     * <p>Constructor for RoleService.</p>
     *
     * @param userRoleService a {@link UserRoleService} object
     */
    public TestRoleService(UserRoleService userRoleService) {
        super(userRoleService);
    }

}
