package cool.scx.ext.util.test;

import cool.scx.ext.util.QRCodeUtils;
import cool.scx.logging.ScxLoggerFactory;
import cool.scx.util.StringUtils;
import org.testng.annotations.Test;

import static java.lang.System.Logger.Level.DEBUG;

public class QRCodeTest {

    static {
        ScxLoggerFactory.rootConfig().setLevel(DEBUG);
    }

    public static void main(String[] args) {
        test1();
    }

    @Test
    public static void test1() {
        //这里返回的是一个 png 的 图片 byte 数组
        byte[] qrCode = QRCodeUtils.getQRCode("123456", 300);

    }

}
