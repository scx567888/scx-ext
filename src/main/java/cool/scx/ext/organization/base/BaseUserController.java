package cool.scx.ext.organization.base;

import cool.scx.core.annotation.FromBody;
import cool.scx.core.annotation.ScxMapping;
import cool.scx.core.enumeration.HttpMethod;
import cool.scx.core.vo.BaseVo;
import cool.scx.core.vo.DataJson;
import cool.scx.core.vo.Json;
import cool.scx.ext.crud.CRUDUpdateParam;
import cool.scx.ext.organization.annotation.Perms;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * <p>Abstract BaseUserController class.</p>
 *
 * @author scx567888
 * @version 1.13.5
 */
public abstract class BaseUserController<T extends BaseUser> {

    protected final Class<T> entityClass;

    private final BaseUserService<T> userService;

    @SuppressWarnings("unchecked")
    /**
     * <p>Constructor for BaseUserController.</p>
     *
     * @param userService a {@link cool.scx.ext.organization.base.BaseUserService} object
     */
    public BaseUserController(BaseUserService<T> userService) {
        this.userService = userService;
        Type genericSuperclass = this.getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            var typeArguments = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
            this.entityClass = (Class<T>) typeArguments[0];
        } else {
            throw new IllegalArgumentException(this.getClass().getName() + " : 必须设置泛型参数 !!!");
        }
    }

    //设置空路由有以下两种方法
    // useNameAsUrl = false ,或 value = "/"

    /**
     * <p>save.</p>
     *
     * @param user a T object
     * @return a {@link cool.scx.core.vo.BaseVo} object
     */
    @ScxMapping(useNameAsUrl = false, method = {HttpMethod.POST})
    public BaseVo save(@FromBody(useAllBody = true) T user) {
        //只有超级管理员才可以修改用户的基本信息
        userService.checkNowLoginUserIsAdmin();
        return DataJson.ok().data(this.userService.saveWithDeptAndRole(user));
    }

    /**
     * <p>update.</p>
     *
     * @param crudUpdateParam a {@link cool.scx.ext.crud.CRUDUpdateParam} object
     * @return a {@link cool.scx.core.vo.BaseVo} object
     */
    @ScxMapping(value = "/", method = {HttpMethod.PUT})
    public BaseVo update(CRUDUpdateParam crudUpdateParam) {
        var user = crudUpdateParam.getBaseModel(entityClass);
        //只有超级管理员才可以修改用户的基本信息
        userService.checkNowLoginUserIsAdmin();
        return DataJson.ok().data(this.userService.updateWithDeptAndRole(user));
    }

    /**
     * <p>checkThatThereIsAtLeastOneAdmin.</p>
     *
     * @param id a {@link java.lang.Long} object
     * @return a {@link cool.scx.core.vo.BaseVo} object
     */
    @ScxMapping(method = HttpMethod.POST)
    public BaseVo checkThatThereIsAtLeastOneAdmin(@FromBody(required = false) Long id) {
        return userService.checkThatThereIsAtLeastOneAdmin(id) ? Json.ok() : Json.fail();
    }

    /**
     * <p>changePasswordByAdminUser.</p>
     *
     * @param newPassword a {@link java.lang.String} object
     * @param userID      a {@link java.lang.Long} object
     * @return a {@link cool.scx.core.vo.BaseVo} object
     */
    @Perms
    @ScxMapping(method = {HttpMethod.PUT})
    public BaseVo changePasswordByAdminUser(@FromBody String newPassword, @FromBody Long userID) {
        return DataJson.ok().data(this.userService.changePasswordByAdminUser(newPassword, userID));
    }

    /**
     * <p>changeUsernameBySelf.</p>
     *
     * @param newUsername a {@link java.lang.String} object
     * @param password    a {@link java.lang.String} object
     * @return a {@link cool.scx.core.vo.BaseVo} object
     */
    @Perms
    @ScxMapping(method = {HttpMethod.PUT})
    public BaseVo changeUsernameBySelf(@FromBody String newUsername, @FromBody String password) {
        return DataJson.ok().data(this.userService.changeUsernameBySelf(newUsername, password));
    }

    /**
     * <p>changePasswordBySelf.</p>
     *
     * @param newPassword a {@link java.lang.String} object
     * @param password    a {@link java.lang.String} object
     * @return a {@link cool.scx.core.vo.BaseVo} object
     */
    @Perms
    @ScxMapping(method = {HttpMethod.PUT})
    public BaseVo changePasswordBySelf(@FromBody String newPassword, @FromBody String password) {
        return DataJson.ok().data(this.userService.changePasswordBySelf(newPassword, password));
    }

}
