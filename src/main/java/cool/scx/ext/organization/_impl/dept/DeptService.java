package cool.scx.ext.organization._impl.dept;

import cool.scx.core.annotation.ScxService;
import cool.scx.ext.organization.dept.BaseDeptService;
import cool.scx.ext.organization.dept.UserDeptService;

@ScxService
public class DeptService extends BaseDeptService<Dept> {

    public DeptService(UserDeptService userDeptService) {
        super(userDeptService);
    }

}
