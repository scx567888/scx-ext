package cool.scx.ext.redirects.test;

import cool.scx.core.Scx;
import cool.scx.core.ScxModule;
import cool.scx.core.enumeration.ScxCoreFeature;
import cool.scx.ext.redirects.RedirectsModule;
import org.testng.annotations.Test;

public class RedirectsModuleTest extends ScxModule {

    public static void main(String[] args) {
        test1();
    }

    @Test
    public static void test1() {
        Scx.builder()
                .setMainClass(RedirectsModuleTest.class)
                .addModule(
                        new RedirectsModule(),
                        new RedirectsModuleTest()
                )
                .configure(ScxCoreFeature.USE_DEVELOPMENT_ERROR_PAGE, true)
                .configure(ScxCoreFeature.USE_SPY, true)
                .run();
    }

}
