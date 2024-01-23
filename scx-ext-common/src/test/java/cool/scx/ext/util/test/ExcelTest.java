package cool.scx.ext.util.test;

import cool.scx.ext.util.Excel;
import org.testng.annotations.Test;

public class ExcelTest {

    public static void main(String[] args) {
        test1();
    }

    @Test
    public static void test1() {
        var excel = Excel.get07Excel("sheet1", 100);
        byte[] bytes = excel.toBytes();
    }

}
