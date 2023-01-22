package cool.scx.test;

import cool.scx.core.Scx;
import cool.scx.core.ScxContext;
import cool.scx.core.ScxModule;
import cool.scx.core.dao.ScxDaoHelper;
import cool.scx.core.enumeration.ScxCoreFeature;
import cool.scx.ext.auth.AuthModule;
import cool.scx.ext.cms.CMSModule;
import cool.scx.ext.crud.CRUDModule;
import cool.scx.ext.fixtable.FixTableModule;
import cool.scx.ext.fss.FSSModule;
import cool.scx.ext.static_server.StaticServerModule;
import cool.scx.ext.ws.WSModule;
import cool.scx.http_client.ScxHttpClientHelper;
import cool.scx.test.auth.TestUserService;
import cool.scx.test.bb.BBService;
import cool.scx.test.website.UserListWebSiteHandler;
import cool.scx.test.website.WriteTimeHandler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.RequestOptions;
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
public class TestModule extends ScxModule {

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        runModule();
        test0();
        test1();
    }

    @BeforeTest
    public static void runModule() {
        Scx.builder()
                .setMainClass(TestModule.class)
                .addModule(
                        new TestModule(),
                        new CMSModule().setWebSiteHandler(UserListWebSiteHandler.class),
                        new WSModule(),
                        new CRUDModule(),
                        new FixTableModule(),
                        new FSSModule(),
                        new AuthModule(),
                        new StaticServerModule())
                .configure(ScxCoreFeature.USE_DEVELOPMENT_ERROR_PAGE, true)
                .run();
    }

    @Test
    public static void test0() throws IOException, InterruptedException {
        var userService = ScxContext.getBean(TestUserService.class);
        System.err.println("访问页面前数据条数 : " + userService.list().size());
        ScxHttpClientHelper.get("http://localhost:8080/");
        System.err.println("访问页面后数据条数 : " + userService.list().size());
    }

    @Test
    public static void test1() throws IOException, InterruptedException {
        var bbService = ScxContext.getBean(BBService.class);
        bbService.test();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        ScxDaoHelper.fixTable();
        WriteTimeHandler.registerHandler();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "SCX_EXT-" + super.name();
    }

}
