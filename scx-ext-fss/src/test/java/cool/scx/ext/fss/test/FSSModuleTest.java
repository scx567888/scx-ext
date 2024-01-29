package cool.scx.ext.fss.test;

import cool.scx.core.Scx;
import cool.scx.core.enumeration.ScxCoreFeature;
import cool.scx.ext.fss.FSSModule;
import org.testng.annotations.Test;

public class FSSModuleTest {

    public static void main(String[] args) {
        test1();
    }

    @Test
    public static void test1() {
        Scx.builder()
                .setMainClass(FSSModuleTest.class)
                .addModule(
                        new FSSModule()
                )
                .configure(ScxCoreFeature.USE_DEVELOPMENT_ERROR_PAGE, true)
                .configure(ScxCoreFeature.USE_SPY, true)
                .run();
    }

}
