package cool.scx.ext.organization._impl;

import cool.scx.core.annotation.ScxService;
import cool.scx.ext.organization.base.BaseDeptService;
import cool.scx.ext.organization.base.UserDeptService;

@ScxService
public final class DeptService extends BaseDeptService<Dept> {

    public DeptService(UserDeptService userDeptService) {
        super(userDeptService);
    }

}
