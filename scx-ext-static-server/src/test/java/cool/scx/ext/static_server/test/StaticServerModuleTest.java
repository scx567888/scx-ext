package cool.scx.ext.static_server.test;

import cool.scx.core.Scx;
import cool.scx.core.enumeration.ScxCoreFeature;
import cool.scx.ext.static_server.StaticServerModule;
import org.testng.annotations.Test;

public class StaticServerModuleTest {

    public static void main(String[] args) {
        test1();
    }

    @Test
    public static void test1() {
        Scx.builder()
                .setMainClass(StaticServerModuleTest.class)
                .addModule(
                        new StaticServerModule()
                )
                .configure(ScxCoreFeature.USE_DEVELOPMENT_ERROR_PAGE, true)
                .configure(ScxCoreFeature.USE_SPY, true)
                .run();
    }

}
