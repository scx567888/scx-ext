package cool.scx.ext.organization;

import cool.scx.ScxModule;
import cool.scx.ext.organization.auth.OrganizationAuth;
import cool.scx.ext.organization.auth.OrganizationAuthWrapper;

/**
 * 拓展模块 (组织机构)
 * 此处只是提供一个最基础的组织机构模型作为参考 ( 不建议在项目中使用 )
 *
 * @author scx567888
 * @version 1.1.11
 */
public class OrganizationModule implements ScxModule {

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        OrganizationAuthWrapper.initAuth();
        OrganizationAuthWrapper.readSessionFromFile();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        OrganizationAuthWrapper.writeSessionToFile();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "SCX_EXT-" + ScxModule.super.name();
    }

}
