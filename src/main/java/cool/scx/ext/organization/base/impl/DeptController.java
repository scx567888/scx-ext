package cool.scx.ext.organization.base.impl;

import cool.scx.core.annotation.ScxMapping;
import cool.scx.ext.organization.base.BaseDeptController;
import cool.scx.ext.organization.base.BaseDeptService;

/**
 * <p>DeptController class.</p>
 *
 * @author scx567888
 * @version 1.13.5
 */
@ScxMapping("/api/dept")
public class DeptController extends BaseDeptController<Dept> {

    /**
     * <p>Constructor for DeptController.</p>
     *
     * @param deptService a
     */
    public DeptController(BaseDeptService<Dept> deptService) {
        super(deptService);
    }

}
