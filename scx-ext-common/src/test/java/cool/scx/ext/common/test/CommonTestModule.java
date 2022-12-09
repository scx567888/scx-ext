package cool.scx.ext.common.test;

import cool.scx.core.Scx;
import cool.scx.core.ScxModule;
import cool.scx.core.dao.ScxDaoHelper;
import cool.scx.core.enumeration.ScxCoreFeature;
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
public class CommonTestModule extends ScxModule {

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
                .setMainClass(CommonTestModule.class)
                .addModule(
                        new CommonTestModule())
                .configure(ScxCoreFeature.USE_DEVELOPMENT_ERROR_PAGE, true)
                .run();
    }

    @Test
    public static void test0() throws IOException, InterruptedException {
        // todo HttpClientHelper 应该支持二进制文件
        var r1 = HttpClientHelper.get("http://localhost:8080/excel");
        var r2 = HttpClientHelper.get("http://localhost:8080/qrcode");
        var r3 = HttpClientHelper.get("http://localhost:8080/zip");
        System.out.println(r1.statusCode() + " - " + r1.body().length());
        System.out.println(r2.statusCode() + " - " + r2.body().length());
        System.out.println(r3.statusCode() + " - " + r3.body().length());
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
