package cool.scx.ext.organization_impl.impl;

import cool.scx.core.annotation.FromBody;
import cool.scx.core.annotation.ScxMapping;
import cool.scx.core.enumeration.HttpMethod;
import cool.scx.core.http.exception.NoPermException;
import cool.scx.core.vo.BaseVo;
import cool.scx.core.vo.DataJson;
import cool.scx.ext.crud.CRUDUpdateParam;
import cool.scx.ext.organization_impl.AuthContext;

/**
 * <p>UserController class.</p>
 *
 * @author scx567888
 * @version 1.13.5
 */
@ScxMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * <p>save.</p>
     *
     * @param user a T object
     * @return a {@link cool.scx.core.vo.BaseVo} object
     * 设置空路由有以下两种方法
     * useNameAsUrl = false ,或 value = "/"
     */
    @ScxMapping(useNameAsUrl = false, method = {HttpMethod.POST})
    public BaseVo add(@FromBody(useAllBody = true) User user) {
        //只有超级管理员才可以修改用户的基本信息
        var loginUser = AuthContext.getCurrentUser();
        if (!loginUser.isAdmin) {
            throw new NoPermException("非管理员无权限修改用户的用户名 !!!");
        }
        return DataJson.ok().data(this.userService.addWithDeptAndRole(user, user.deptIDs, user.roleIDs));
    }

    /**
     * <p>update.</p>
     *
     * @param crudUpdateParam a {@link cool.scx.ext.crud.CRUDUpdateParam} object
     * @return a {@link cool.scx.core.vo.BaseVo} object
     */
    @ScxMapping(value = "/", method = {HttpMethod.PUT})
    public BaseVo update(CRUDUpdateParam crudUpdateParam) {
        //只有超级管理员才可以修改用户的基本信息
        var loginUser = AuthContext.getCurrentUser();
        if (!loginUser.isAdmin) {
            throw new NoPermException("非管理员无权限修改用户的用户名 !!!");
        }
        var user = crudUpdateParam.getBaseModel(User.class);
        return DataJson.ok().data(this.userService.updateWithDeptAndRole(user, user.deptIDs, user.roleIDs));
    }

}
