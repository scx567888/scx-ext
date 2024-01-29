package cool.scx.ext.crud.test;

import cool.scx.core.Scx;
import cool.scx.core.ScxModule;
import cool.scx.core.enumeration.ScxCoreFeature;
import cool.scx.ext.crud.CRUDModule;
import org.testng.annotations.Test;

public class CRUDModuleTest extends ScxModule {

    public static void main(String[] args) {
        test1();
    }

    @Test
    public static void test1() {
        Scx.builder()
                .setMainClass(CRUDModuleTest.class)
                .addModule(
                        new CRUDModule(),
                        new CRUDModuleTest()
                )
                .configure(ScxCoreFeature.USE_DEVELOPMENT_ERROR_PAGE, true)
                .configure(ScxCoreFeature.USE_SPY, true)
                .run();
    }

    @Override
    public void start(Scx scx) {
        scx.fixTable();
    }

}
