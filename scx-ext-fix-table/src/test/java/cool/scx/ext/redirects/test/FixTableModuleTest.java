package cool.scx.ext.redirects.test;

import cool.scx.core.Scx;
import cool.scx.core.ScxModule;
import cool.scx.core.enumeration.ScxCoreFeature;
import cool.scx.ext.fix_table.FixTableModule;
import org.testng.annotations.Test;

public class FixTableModuleTest extends ScxModule {

    public static void main(String[] args) {
        test1();
    }

    @Test
    public static void test1() {
        Scx.builder()
                .setMainClass(FixTableModuleTest.class)
                .addModule(
                        new FixTableModule(),
                        new FixTableModuleTest()
                )
                .configure(ScxCoreFeature.USE_DEVELOPMENT_ERROR_PAGE, true)
                .configure(ScxCoreFeature.USE_SPY, true)
                .run();
    }

}
