package cool.scx.ext.organization._impl;

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
public final class DeptService extends BaseDeptService<Dept> {

    /**
     * <p>Constructor for DeptService.</p>
     *
     * @param userDeptService a {@link cool.scx.ext.organization.base.UserDeptService} object
     */
    public DeptService(UserDeptService userDeptService) {
        super(userDeptService);
    }

}
