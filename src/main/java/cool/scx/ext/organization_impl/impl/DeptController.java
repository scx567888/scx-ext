package cool.scx.ext.organization_impl.impl;

import cool.scx.core.annotation.FromBody;
import cool.scx.core.annotation.ScxMapping;
import cool.scx.core.enumeration.HttpMethod;
import cool.scx.core.vo.Json;

/**
 * <p>DeptController class.</p>
 *
 * @author scx567888
 * @version 1.13.5
 */
@ScxMapping("/api/dept")
public class DeptController {

    private final DeptService deptService;

    public DeptController(DeptService deptService) {
        this.deptService = deptService;
    }

    /**
     * <p>deleteDeptWithChildren.</p>
     *
     * @param id a {@link java.lang.Long} object
     * @return a {@link cool.scx.core.vo.Json} object
     */
    @ScxMapping(method = HttpMethod.DELETE)
    public Json deleteDeptWithChildren(@FromBody Long id) {
        deptService.deleteDeptWithChildren(id);
        return Json.ok();
    }

}
