package cool.scx.test;

import cool.scx.Scx;
import cool.scx.ScxContext;
import cool.scx.ScxModule;
import cool.scx.dao.ScxDaoHelper;
import cool.scx.enumeration.ScxFeature;
import cool.scx.ext.cms.CMSModule;
import cool.scx.ext.core.CoreModule;
import cool.scx.ext.crud.CRUDModule;
import cool.scx.ext.fixtable.FixTableModule;
import cool.scx.ext.fss.FSSModule;
import cool.scx.test.auth.TestAuth;
import cool.scx.test.user.UserService;
import cool.scx.test.website.UserListWebSiteHandler;
import cool.scx.test.website.WriteTimeHandler;
import cool.scx.util.HttpClientHelper;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * <p>TestModule class.</p>
 *
 * @author scx567888
 * @version 1.3.14
 * @since 1.3.14
 */
public class TestModule implements ScxModule {

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects
     */
    public static void main(String[] args) {
        runModule();
    }

    @BeforeTest
    public static void runModule() {
        Scx.builder()
                .setMainClass(TestModule.class)
                .configure(ScxFeature.USE_DEVELOPMENT_ERROR_PAGE, true)
                .addModules(
                        new TestModule(),
                        new CMSModule().setWebSiteHandler(UserListWebSiteHandler.class),
                        new CoreModule(),
                        new CRUDModule(),
                        new FixTableModule(),
                        new FSSModule())
                .build().run();
    }

    @Test
    public static void test0() throws IOException, InterruptedException {
        var userService = ScxContext.beanFactory().getBean(UserService.class);
        System.err.println("访问页面前数据条数 : " + userService.list().size());
        HttpClientHelper.get("http://localhost:8080/");
        System.err.println("访问页面后数据条数 : " + userService.list().size());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        ScxDaoHelper.fixTable();
        WriteTimeHandler.registerHandler();
        TestAuth.initAuth();
        TestAuth.readSessionFromFile();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        TestAuth.writeSessionToFile();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "SCX_EXT-" + ScxModule.super.name();
    }

}
