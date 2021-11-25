package cool.scx.ext.organization;

import cool.scx.annotation.FromBody;
import cool.scx.annotation.ScxMapping;
import cool.scx.bo.Query;
import cool.scx.enumeration.HttpMethod;
import cool.scx.vo.Json;

@ScxMapping("api/dept")
public class DeptController {

    private final DeptService deptService;

    public DeptController(DeptService deptService) {
        this.deptService = deptService;
    }

    @ScxMapping(method = HttpMethod.DELETE)
    public Json deleteDept(@FromBody Long id) {
        deleteDeptWithChildren(id);
        return Json.ok();
    }

    /**
     * 递归删除 部门节点
     *
     * @param id id
     */
    private void deleteDeptWithChildren(Long id) {
        //先删除当前节点
        deptService.delete(id);
        //拼一个查询条件先
        var q = new Query().equal("parentID", id);
        //找一下当前节点的子节点
        var parentDeptList = deptService.list(q);
        //在删除所有当前节点的子节点 注意顺序不要乱不然删完了就查不到了
        deptService.delete(q);
        //循环删除 当前子节点下的所有(孙)节点
        for (var parentDept : parentDeptList) {
            deleteDeptWithChildren(parentDept.id);
        }
    }

}
