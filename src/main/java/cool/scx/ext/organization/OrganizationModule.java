package cool.scx.ext.organization;

import cool.scx.ScxModule;
import cool.scx.ext.organization.auth.ScxAuth;

/**
 * <p>OrganizationModule class.</p>
 *
 * @author scx567888
 * @version 1.11.8
 */
public class OrganizationModule implements ScxModule {

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        ScxAuth.initAuth();// 初始化认证模块
        ScxAuth.readSessionFromFile();//从文件中读取 session
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        ScxAuth.writeSessionToFile();
    }

}
