package cool.scx.ext.static_server.test;

import cool.scx.core.Scx;
import cool.scx.core.ScxModule;
import cool.scx.core.dao.ScxDaoHelper;
import cool.scx.core.enumeration.ScxCoreFeature;
import cool.scx.ext.static_server.StaticServerModule;
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
public class StaticServerTestModule extends ScxModule {

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
                .setMainClass(StaticServerTestModule.class)
                .addModule(
                        new StaticServerTestModule(),
                        new StaticServerModule())
                .configure(ScxCoreFeature.USE_DEVELOPMENT_ERROR_PAGE, true)
                .run();
    }

    @Test
    public static void test0() throws IOException, InterruptedException {
        HttpClientHelper.get("http://localhost:8080/");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        ScxDaoHelper.fixTable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "SCX_EXT-" + super.name();
    }

}
