package cool.scx.ext.organization;

import cool.scx.core.ScxModule;
import cool.scx.core.base.BaseModel;
import cool.scx.core.base.BaseModelService;
import cool.scx.ext.organization._impl.*;
import cool.scx.ext.organization.auth.ScxAuth;
import cool.scx.ext.organization.base.BaseDeptService;
import cool.scx.ext.organization.base.BaseRoleService;
import cool.scx.ext.organization.base.BaseUserService;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>OrganizationModule class.</p>
 *
 * @author scx567888
 * @version 1.11.8
 */
public class OrganizationModule extends ScxModule {

    /**
     * 指定使用的 userService 类
     */
    private Class<? extends BaseUserService<?>> userServiceClass = UserService.class;

    /**
     * 指定使用的 deptService 类
     */
    private Class<? extends BaseDeptService<?>> deptServiceClass = DeptService.class;

    /**
     * 指定使用的 roleService 类
     */
    private Class<? extends BaseRoleService<?>> roleServiceClass = RoleService.class;

    /**
     * 指定是否启动默认的 User (如果设置为 false,并且未指定具体的 userServiceClass 时则会报错)
     */
    private boolean enableDefaultUser = true;

    /**
     * 指定是否启动默认的 Dept (如果设置为 false, 并且未指定具体的 deptServiceClass 时则会报错)
     */
    private boolean enableDefaultDept = true;

    /**
     * 指定是否启动默认的 Role (如果设置为 false, 并且未指定具体的 roleServiceClass 时则会报错)
     */
    private boolean enableDefaultRole = true;

    /**
     * 指定是否启动默认的 AuthApi (如果设置为 false, 或者关闭任意一个默认实现时则不启用)
     */
    private boolean enableDefaultAuthApi = true;

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

    public boolean enableDefaultUser() {
        return enableDefaultUser && userServiceClass == UserService.class;
    }

    public OrganizationModule setEnableDefaultUser(boolean enableDefaultUser) {
        this.enableDefaultUser = enableDefaultUser;
        return this;
    }

    public boolean enableDefaultDept() {
        return enableDefaultDept && deptServiceClass == DeptService.class;
    }

    public OrganizationModule setEnableDefaultDept(boolean enableDefaultDept) {
        this.enableDefaultDept = enableDefaultDept;
        return this;
    }

    public boolean enableDefaultRole() {
        return enableDefaultRole && roleServiceClass == RoleService.class;
    }

    public OrganizationModule setEnableDefaultRole(boolean enableDefaultRole) {
        this.enableDefaultRole = enableDefaultRole;
        return this;
    }

    public boolean enableDefaultAuthApi() {
        return enableDefaultAuthApi && enableDefaultUser();
    }

    public OrganizationModule setEnableDefaultAuthApi(boolean enableDefaultAuthApi) {
        this.enableDefaultAuthApi = enableDefaultAuthApi;
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
        var list = new ArrayList<>(super.scxMappingClassList());
        removeClass(list);
        return list;
    }

    @Override
    public List<Class<? extends BaseModel>> scxBaseModelClassList() {
        var list = new ArrayList<>(super.scxBaseModelClassList());
        removeClass(list);
        return list;
    }

    @Override
    public List<Class<? extends BaseModelService<?>>> scxBaseModelServiceClassList() {
        var list = new ArrayList<>(super.scxBaseModelServiceClassList());
        removeClass(list);
        return list;
    }

    private void removeClass(List<?> list) {
        if (!enableDefaultAuthApi()) {
            list.remove(AuthController.class);
        }
        if (!enableDefaultUser()) {
            list.remove(User.class);
            list.remove(UserService.class);
        }
        if (!enableDefaultDept()) {
            list.remove(Dept.class);
            list.remove(DeptService.class);
        }
        if (!enableDefaultRole()) {
            list.remove(Role.class);
            list.remove(RoleService.class);
        }
    }

    @Override
    public List<Class<?>> scxBeanClassList() {
        var list = new ArrayList<>(super.scxBeanClassList());
        removeClass(list);
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
