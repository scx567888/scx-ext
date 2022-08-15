package cool.scx.ext.organization.base;

import cool.scx.core.annotation.FromBody;
import cool.scx.core.annotation.ScxMapping;
import cool.scx.core.enumeration.HttpMethod;
import cool.scx.core.vo.Json;

/**
 * <p>Abstract BaseDeptController class.</p>
 *
 * @author scx567888
 * @version 1.13.5
 */
public abstract class BaseDeptController<T extends BaseDept> {

    private final BaseDeptService<T> deptService;

    /**
     * <p>Constructor for DeptController.</p>
     *
     * @param deptService a {@link cool.scx.example.dept.DeptService} object
     */
    public BaseDeptController(BaseDeptService<T> deptService) {
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
