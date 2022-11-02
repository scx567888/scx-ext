package cool.scx.ext;

import cool.scx.core.Scx;
import cool.scx.ext.cms.CMSModule;
import cool.scx.ext.crud.CRUDModule;
import cool.scx.ext.fixtable.FixTableModule;
import cool.scx.ext.fss.FSSModule;
import cool.scx.ext.organization.OrganizationModule;
import cool.scx.ext.static_server.StaticServerModule;
import cool.scx.ext.ws.WSModule;

/**
 * 运行所有核心包提供的模块 (演示用,不要用于生产环境)
 *
 * @author scx567888
 * @version 1.1.11
 */
public class AllExtApp {

    /**
     * 核心启动方法
     *
     * @param args 外部参数
     */
    public static void main(String[] args) {
        Scx.builder()
                .setMainClass(AllExtApp.class)
                .addModule(
                        new CMSModule(),
                        new WSModule(),
                        new CRUDModule(),
                        new FixTableModule(),
                        new FSSModule(),
                        new OrganizationModule(),
                        new StaticServerModule())
                .setArgs(args)
                .run();
    }

}
