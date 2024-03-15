package cool.scx.ext.common.test;

import cool.scx.ext.common.util.QRCodeUtils;
import org.testng.annotations.Test;

public class QRCodeTest {

    public static void main(String[] args) {
        test1();
    }

    @Test
    public static void test1() {
        //这里返回的是一个 png 的 图片 byte 数组
        byte[] qrCode = QRCodeUtils.getQRCode("123456", 300);

    }

}
