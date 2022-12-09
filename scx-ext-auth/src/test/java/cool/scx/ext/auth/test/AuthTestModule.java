package cool.scx.ext.auth.test;

import cool.scx.core.Scx;
import cool.scx.core.ScxContext;
import cool.scx.core.ScxModule;
import cool.scx.core.dao.ScxDaoHelper;
import cool.scx.core.enumeration.ScxCoreFeature;
import cool.scx.ext.auth.AuthModule;
import cool.scx.ext.auth.test.auth.TestUserService;
import cool.scx.ext.auth.test.website.WriteTimeHandler;
import cool.scx.ext.ws.WSModule;
import cool.scx.util.http.HttpClientHelper;
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
public class AuthTestModule extends ScxModule {

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        runModule();
        test0();
    }

    @BeforeTest
    public static void runModule() {
        Scx.builder()
                .setMainClass(AuthTestModule.class)
                .addModule(
                        new AuthTestModule(),
                        new WSModule(),
                        new AuthModule())
                .configure(ScxCoreFeature.USE_DEVELOPMENT_ERROR_PAGE, true)
                .run();
    }

    @Test
    public static void test0() throws IOException, InterruptedException {
        var userService = ScxContext.getBean(TestUserService.class);
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
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "SCX_EXT-" + super.name();
    }

}
