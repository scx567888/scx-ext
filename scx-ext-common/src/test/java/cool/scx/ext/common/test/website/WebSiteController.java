package cool.scx.ext.common.test.website;

import cool.scx.core.annotation.FromQuery;
import cool.scx.core.annotation.ScxMapping;
import cool.scx.core.enumeration.HttpMethod;
import cool.scx.core.enumeration.RawType;
import cool.scx.core.vo.BaseVo;
import cool.scx.core.vo.Download;
import cool.scx.core.vo.Html;
import cool.scx.core.vo.Raw;
import cool.scx.ext.util.Excel;
import cool.scx.ext.util.QRCodeUtils;
import cool.scx.util.RandomUtils;
import cool.scx.util.StringUtils;
import cool.scx.util.ZipBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 简单测试
 *
 * @author scx567888
 * @version 0.3.6
 * @since 1.3.14
 */
@ScxMapping("/")
public class WebSiteController {

    @ScxMapping(value = "/",method = HttpMethod.GET)
    public static Html index() throws IOException {
        return Html.of("index");
    }

    /**
     * <p>excel.</p>
     *
     * @return a {@link BaseVo} object
     */
    @ScxMapping(method = HttpMethod.GET)
    public BaseVo excel() {
        Excel excel = Excel.get07Excel("测试1", 1000);
        for (int i = 0; i < 999; i = i + 1) {
            for (int j = 0; j < 99; j = j + 1) {
                excel.setCellValue(i, j, "测试数据" + i + "-" + j);
            }
        }
        excel.mergedRegion(2, 2, 1, 10);
        excel.setCellValue(2, 2, "这是合并的单元格");

        return Download.of(excel.toBytes(), "测试 Excel 😎👀😃✨😜.xlsx");
    }

    /**
     * <p>qrcode.</p>
     *
     * @param value a {@link java.lang.String} object
     * @return a {@link BaseVo} object
     */
    @ScxMapping(method = HttpMethod.GET)
    public BaseVo qrcode(@FromQuery(required = false) String value) {
        if (StringUtils.isBlank(value)) {
            value = RandomUtils.randomUUID() + " : 前面的是UUID";
        }
        //这里返回的是一个 png 的 图片 byte 数组
        byte[] qrCode = QRCodeUtils.getQRCode(value, 300);

        return Raw.of(qrCode, RawType.PNG);
    }

    /**
     * <p>qrcode.</p>
     *
     * @return a {@link BaseVo} object
     */
    @ScxMapping(method = HttpMethod.GET)
    public BaseVo zip() throws Exception {
        var zipBuilder = new ZipBuilder()
                .put("第一个目录/第二个目录/第二个目录中的文件.txt", "文件内容".getBytes(StandardCharsets.UTF_8))
                .put("这是一系列空目录/这是一系列空目录/这是一系列空目录/这是一系列空目录/这是一系列空目录")
                .put("这不是一系列空目录/这不是一系列空目录/这不是一系列空目录/这不是一系列空目录/这不是一系列空目录/一个二维码图片.png", QRCodeUtils.getQRCode("一个二维码图片", 300));
        byte[] bytes = zipBuilder.toZipBytes();
        return Download.of(bytes, "测试压缩包.zip");
    }


}
