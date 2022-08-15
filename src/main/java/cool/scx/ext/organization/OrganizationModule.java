package cool.scx.ext.organization;

import cool.scx.core.ScxModule;
import cool.scx.core.base.BaseModel;
import cool.scx.core.base.BaseModelService;
import cool.scx.ext.organization._impl.*;
import cool.scx.ext.organization.auth.ScxAuth;
import cool.scx.ext.organization.base.BaseDeptService;
import cool.scx.ext.organization.base.BaseRoleService;
import cool.scx.ext.organization.base.BaseUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>OrganizationModule class.</p>
 *
 * @author scx567888
 * @version 1.11.8
 */
public class OrganizationModule extends ScxModule {

    private final Logger logger = LoggerFactory.getLogger(OrganizationModule.class);

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

    /**
     * <p>Getter for the field <code>userServiceClass</code>.</p>
     *
     * @return a {@link java.lang.Class} object
     */
    public Class<? extends BaseUserService<?>> getUserServiceClass() {
        return userServiceClass;
    }

    /**
     * <p>Setter for the field <code>userServiceClass</code>.</p>
     *
     * @param userServiceClass a {@link java.lang.Class} object
     * @return a {@link cool.scx.ext.organization.OrganizationModule} object
     */
    public OrganizationModule setUserServiceClass(Class<? extends BaseUserService<?>> userServiceClass) {
        this.userServiceClass = userServiceClass;
        return this;
    }

    /**
     * <p>Getter for the field <code>deptServiceClass</code>.</p>
     *
     * @return a {@link java.lang.Class} object
     */
    public Class<? extends BaseDeptService<?>> getDeptServiceClass() {
        return deptServiceClass;
    }

    /**
     * <p>Setter for the field <code>deptServiceClass</code>.</p>
     *
     * @param deptServiceClass a {@link java.lang.Class} object
     * @return a {@link cool.scx.ext.organization.OrganizationModule} object
     */
    public OrganizationModule setDeptServiceClass(Class<? extends BaseDeptService<?>> deptServiceClass) {
        this.deptServiceClass = deptServiceClass;
        return this;
    }

    /**
     * <p>Getter for the field <code>roleServiceClass</code>.</p>
     *
     * @return a {@link java.lang.Class} object
     */
    public Class<? extends BaseRoleService<?>> getRoleServiceClass() {
        return roleServiceClass;
    }

    /**
     * <p>Setter for the field <code>roleServiceClass</code>.</p>
     *
     * @param roleServiceClass a {@link java.lang.Class} object
     * @return a {@link cool.scx.ext.organization.OrganizationModule} object
     */
    public OrganizationModule setRoleServiceClass(Class<? extends BaseRoleService<?>> roleServiceClass) {
        this.roleServiceClass = roleServiceClass;
        return this;
    }

    /**
     * <p>enableDefaultUser.</p>
     *
     * @return a boolean
     */
    public boolean enableDefaultUser() {
        return enableDefaultUser && userServiceClass == UserService.class;
    }

    /**
     * <p>Setter for the field <code>enableDefaultUser</code>.</p>
     *
     * @param enableDefaultUser a boolean
     * @return a {@link cool.scx.ext.organization.OrganizationModule} object
     */
    public OrganizationModule setEnableDefaultUser(boolean enableDefaultUser) {
        this.enableDefaultUser = enableDefaultUser;
        return this;
    }

    /**
     * <p>enableDefaultDept.</p>
     *
     * @return a boolean
     */
    public boolean enableDefaultDept() {
        return enableDefaultDept && deptServiceClass == DeptService.class;
    }

    /**
     * <p>Setter for the field <code>enableDefaultDept</code>.</p>
     *
     * @param enableDefaultDept a boolean
     * @return a {@link cool.scx.ext.organization.OrganizationModule} object
     */
    public OrganizationModule setEnableDefaultDept(boolean enableDefaultDept) {
        this.enableDefaultDept = enableDefaultDept;
        return this;
    }

    /**
     * <p>enableDefaultRole.</p>
     *
     * @return a boolean
     */
    public boolean enableDefaultRole() {
        return enableDefaultRole && roleServiceClass == RoleService.class;
    }

    /**
     * <p>Setter for the field <code>enableDefaultRole</code>.</p>
     *
     * @param enableDefaultRole a boolean
     * @return a {@link cool.scx.ext.organization.OrganizationModule} object
     */
    public OrganizationModule setEnableDefaultRole(boolean enableDefaultRole) {
        this.enableDefaultRole = enableDefaultRole;
        return this;
    }

    /**
     * <p>enableDefaultAuthApi.</p>
     *
     * @return a boolean
     */
    public boolean enableDefaultAuthApi() {
        return enableDefaultAuthApi && enableDefaultUser();
    }

    /**
     * <p>Setter for the field <code>enableDefaultAuthApi</code>.</p>
     *
     * @param enableDefaultAuthApi a boolean
     * @return a {@link cool.scx.ext.organization.OrganizationModule} object
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Class<?>> scxMappingClassList() {
        var list = new ArrayList<>(super.scxMappingClassList());
        removeClass(list, "ScxMappingClassList");
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Class<? extends BaseModel>> scxBaseModelClassList() {
        var list = new ArrayList<>(super.scxBaseModelClassList());
        removeClass(list, "ScxBaseModelClassList");
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Class<? extends BaseModelService<?>>> scxBaseModelServiceClassList() {
        var list = new ArrayList<>(super.scxBaseModelServiceClassList());
        removeClass(list, "ScxBaseModelServiceClassList");
        return list;
    }

    /**
     * <p>removeClass.</p>
     *
     * @param list a {@link java.util.List} object
     * @param name a {@link java.lang.String} object
     */
    private void removeClass(List<?> list, String name) {
        if (!enableDefaultAuthApi()) {
            boolean remove = list.remove(AuthController.class);
            if (remove) {
                logger.info("已从 {} 中移除 {}", name, AuthController.class);
            }
        }
        if (!enableDefaultUser()) {
            boolean remove = list.remove(User.class);
            boolean remove1 = list.remove(UserService.class);
            //因为 Account 依赖于默认的 User 所以我们在这里排除
            boolean remove2 = list.remove(Account.class);
            boolean remove3 = list.remove(AccountService.class);
            if (remove) {
                logger.info("已从 {} 中移除 {}", name, User.class);
            }
            if (remove1) {
                logger.info("已从 {} 中移除 {}", name, UserService.class);
            }
            if (remove2) {
                logger.info("已从 {} 中移除 {}", name, Account.class);
            }
            if (remove3) {
                logger.info("已从 {} 中移除 {}", name, AccountService.class);
            }

        }
        if (!enableDefaultDept()) {
            boolean remove = list.remove(Dept.class);
            boolean remove1 = list.remove(DeptService.class);
            if (remove) {
                logger.info("已从 {} 中移除 {}", name, Dept.class);
            }
            if (remove1) {
                logger.info("已从 {} 中移除 {}", name, DeptService.class);
            }
        }
        if (!enableDefaultRole()) {
            boolean remove = list.remove(Role.class);
            boolean remove1 = list.remove(RoleService.class);

            if (remove) {
                logger.info("已从 {} 中移除 {}", name, Role.class);
            }
            if (remove1) {
                logger.info("已从 {} 中移除 {}", name, RoleService.class);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Class<?>> scxBeanClassList() {
        var list = new ArrayList<>(super.scxBeanClassList());
        removeClass(list, "ScxBeanClassList");
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
