package cool.scx.ext.organization.role;

import cool.scx.annotation.ScxModel;
import cool.scx.ext.crud.annotation.UseCRUDApi;
import cool.scx.ext.organization.auth.PermsModel;

/**
 * 角色
 *
 * @author scx567888
 * @version 0.3.6
 */
@UseCRUDApi
@ScxModel(tablePrefix = "organization")
public class Role extends PermsModel {

    /**
     * 角色名称
     */
    public String roleName;

}
