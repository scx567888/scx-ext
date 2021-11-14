package cool.scx.ext.organization;

import cool.scx.ScxModule;
import cool.scx.ext.organization.auth.OrganizationAuthWrapper;

/**
 * 拓展模块 (组织机构)
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
        OrganizationConfig.initConfig();
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
