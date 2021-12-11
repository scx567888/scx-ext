package cool.scx.ext.organization.dept;

import cool.scx.annotation.FromBody;
import cool.scx.annotation.ScxMapping;
import cool.scx.enumeration.HttpMethod;
import cool.scx.vo.Json;

@ScxMapping("api/dept")
public class DeptController {

    private final DeptService deptService;

    public DeptController(DeptService deptService) {
        this.deptService = deptService;
    }

    @ScxMapping(method = HttpMethod.DELETE)
    public Json deleteDeptWithChildren(@FromBody Long id) {
        deptService.deleteDeptWithChildren(id);
        return Json.ok();
    }

}
