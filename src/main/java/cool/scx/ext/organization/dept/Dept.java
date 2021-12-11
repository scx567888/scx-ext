package cool.scx.ext.organization.dept;

import cool.scx.annotation.ScxModel;
import cool.scx.base.BaseModel;

import java.util.List;

/**
 * 部门
 *
 * @author scx567888
 * @version 0.3.6
 */
@ScxModel(tablePrefix = "organization")
public class Dept extends BaseModel {

    /**
     * 部门名称
     */
    public String deptName;

    /**
     * 部门权限
     */
    public List<String> perms;

    /**
     * 父id 用作构建树形结构
     */
    public Long parentID;

}
