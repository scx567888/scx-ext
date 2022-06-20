package cool.scx.ext.organization;

import cool.scx.ScxModule;
import cool.scx.ext.organization.auth.ScxAuth;

public class OrganizationModule implements ScxModule {

    @Override
    public void start() {
        ScxAuth.initAuth();// 初始化认证模块
        ScxAuth.readSessionFromFile();//从文件中读取 session
    }

    @Override
    public void stop() {
        ScxAuth.writeSessionToFile();
    }

}
