package cool.scx.ext.redirects.test;

import cool.scx.core.Scx;
import cool.scx.core.ScxModule;
import cool.scx.core.enumeration.ScxCoreFeature;
import cool.scx.ext.redirects.RedirectsModule;
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
public class RedirectsTestModule extends ScxModule {

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
                .setMainClass(RedirectsTestModule.class)
                .addModule(
                        new RedirectsTestModule(),
                        new RedirectsModule())
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
    public String name() {
        return "SCX_EXT-" + super.name();
    }

}
