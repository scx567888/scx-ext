package cool.scx.ext.organization;

import cool.scx.core.ScxModule;
import cool.scx.ext.organization._impl.DeptService;
import cool.scx.ext.organization._impl.RoleService;
import cool.scx.ext.organization._impl.UserService;
import cool.scx.ext.organization.api.ScxAuthController;
import cool.scx.ext.organization.auth.ScxAuth;
import cool.scx.ext.organization.base.BaseDeptService;
import cool.scx.ext.organization.base.BaseRoleService;
import cool.scx.ext.organization.base.BaseUserService;

import java.util.List;

/**
 * <p>OrganizationModule class.</p>
 *
 * @author scx567888
 * @version 1.11.8
 */
public class OrganizationModule extends ScxModule {

    /**
     * 有时候我们需要拓展内部的 UserService
     */
    private Class<? extends BaseUserService<?>> userServiceClass = UserService.class;
    private Class<? extends BaseDeptService<?>> deptServiceClass = DeptService.class;
    private Class<? extends BaseRoleService<?>> roleServiceClass = RoleService.class;

    private boolean useDefaultAuthApi = true;

    public OrganizationModule() {

    }

    public boolean getUseDefaultAuthApi() {
        return useDefaultAuthApi;
    }

    public void setUseDefaultAuthApi(boolean useDefaultAuthApi) {
        this.useDefaultAuthApi = useDefaultAuthApi;
    }

    public Class<? extends BaseUserService<?>> getUserServiceClass() {
        return userServiceClass;
    }

    public OrganizationModule setUserServiceClass(Class<? extends BaseUserService<?>> userServiceClass) {
        this.userServiceClass = userServiceClass;
        return this;
    }

    public Class<? extends BaseDeptService<?>> getDeptServiceClass() {
        return deptServiceClass;
    }

    public OrganizationModule setDeptServiceClass(Class<? extends BaseDeptService<?>> deptServiceClass) {
        this.deptServiceClass = deptServiceClass;
        return this;
    }

    public Class<? extends BaseRoleService<?>> getRoleServiceClass() {
        return roleServiceClass;
    }

    public OrganizationModule setRoleServiceClass(Class<? extends BaseRoleService<?>> roleServiceClass) {
        this.roleServiceClass = roleServiceClass;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        ScxAuth.initAuth(userServiceClass, deptServiceClass, roleServiceClass);// 初始化认证模块
        ScxAuth.readSessionFromFile();//从文件中读取 session
    }

    @Override
    public List<Class<?>> scxMappingClassList() {
        var list = super.scxMappingClassList();
        if (!useDefaultAuthApi) {
            list.remove(ScxAuthController.class);
        }
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        ScxAuth.writeSessionToFile();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "SCX_EXT-" + super.name();
    }

}
