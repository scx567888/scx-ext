package cool.scx.ext.util.test;

import cool.scx.ext.util.Excel;
import cool.scx.logging.ScxLogger;
import cool.scx.logging.ScxLoggerFactory;
import org.testng.annotations.Test;

import static java.lang.System.Logger.Level.DEBUG;

public class ExcelTest {

    static {
        ScxLoggerFactory.rootConfig().setLevel(DEBUG);
    }

    public static void main(String[] args) {
        test1();
    }

    @Test
    public static void test1() {
        var excel = Excel.get07Excel("sheet1", 100);
        byte[] bytes = excel.toBytes();
    }

}
