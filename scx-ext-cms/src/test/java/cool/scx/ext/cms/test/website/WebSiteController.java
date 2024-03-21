package cool.scx.ext.cms.test.website;

import cool.scx.common.standard.HttpMethod;
import cool.scx.common.util.CryptoUtils;
import cool.scx.common.util.RandomUtils;
import cool.scx.common.util.StringUtils;
import cool.scx.common.util.zip.ZipBuilder;
import cool.scx.core.ScxContext;
import cool.scx.ext.cms.channel.Channel;
import cool.scx.ext.cms.channel.ChannelService;
import cool.scx.ext.cms.content.Content;
import cool.scx.ext.cms.content.ContentService;
import cool.scx.ext.cms.test.auth.TestUser;
import cool.scx.ext.cms.test.auth.TestUserService;
import cool.scx.ext.common.util.Excel;
import cool.scx.ext.common.util.QRCodeUtils;
import cool.scx.mvc.annotation.FromQuery;
import cool.scx.mvc.annotation.ScxRoute;
import cool.scx.mvc.vo.BaseVo;
import cool.scx.mvc.vo.Download;
import cool.scx.mvc.vo.Html;
import cool.scx.mvc.vo.Raw;
import io.vertx.ext.web.RoutingContext;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static cool.scx.common.standard.FileFormat.PNG;
import static cool.scx.common.standard.ScxDateTimeFormatter.yyyy_MM_dd_HH_mm_ss;

/**
 * 简单测试
 *
 * @author scx567888
 * @version 0.3.6
 * @since 1.3.14
 */
@ScxRoute("")
public class WebSiteController {

    @ScxRoute(methods = HttpMethod.GET)
    public static void TestTransaction(RoutingContext ctx) throws Exception {
        var sb = new StringBuilder();
        var bean = ScxContext.getBean(TestUserService.class);
        try {
            ScxContext.sqlRunner().autoTransaction(() -> {
                sb.append("事务开始前数据库中 数据条数 : ").append(bean.find().size()).append("</br>");
                sb.append("现在插入 1 数据条数").append("</br>");
                var u = new TestUser();
                u.password = CryptoUtils.encryptPassword("123456");
                u.username = "唯一name";
                bean.add(u);
                sb.append("现在数据库中数据条数 : ").append(bean.find().size()).append("</br>");
                bean.add(u);
            });
        } catch (Exception e) {
            sb.append("出错了 后滚后数据库中数据条数 : ").append(bean.find().size());
        }
        Html.ofString(sb.toString()).accept(ctx, ScxContext.scxMvc().templateHandler());
    }


    /**
     * <p>excel.</p>
     *
     * @return a {@link BaseVo} object
     */
    @ScxRoute(methods = HttpMethod.GET)
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
     * @param value a {@link String} object
     * @return a {@link BaseVo} object
     */
    @ScxRoute(methods = HttpMethod.GET)
    public BaseVo qrcode(@FromQuery(required = false) String value) {
        if (StringUtils.isBlank(value)) {
            value = RandomUtils.randomUUID() + " : 前面的是UUID";
        }
        //这里返回的是一个 png 的 图片 byte 数组
        byte[] qrCode = QRCodeUtils.getQRCode(value, 300);

        return Raw.of(qrCode, PNG);
    }

    /**
     * <p>qrcode.</p>
     *
     * @return a {@link BaseVo} object
     */
    @ScxRoute(methods = HttpMethod.GET)
    public BaseVo zip() throws Exception {
        var zipBuilder = new ZipBuilder()
                .put("第一个目录/第二个目录/第二个目录中的文件.txt", "文件内容".getBytes(StandardCharsets.UTF_8))
                .put("这是一系列空目录/这是一系列空目录/这是一系列空目录/这是一系列空目录/这是一系列空目录")
                .put("这不是一系列空目录/这不是一系列空目录/这不是一系列空目录/这不是一系列空目录/这不是一系列空目录/一个二维码图片.png", QRCodeUtils.getQRCode("一个二维码图片", 300));
        byte[] bytes = zipBuilder.toBytes();
        return Download.of(bytes, "测试压缩包.zip");
    }

    @ScxRoute(methods = HttpMethod.GET)
    public Object initCMS() {
        var s = ScxContext.getBean(ChannelService.class);
        var c = ScxContext.getBean(ContentService.class);
        for (int i = 0; i < 3; i = i + 1) {
            var s1 = new Channel();
            s1.channelName = "早间新闻" + i;
            s1.channelPath = "news" + i;
            var save1 = s.add(s1);
            for (int j = 0; j < 10; j = j + 1) {
                var c1 = new Content();
                c1.content = "重大早间新闻的内容<span style='color:green'>绿色的文字</span>" + j;
                c1.contentTitle = "重大早间新闻的标题👍" + j;
                c1.channelID = save1.id;
                c.add(c1);
            }
        }

        for (int i = 0; i < 3; i = i + 1) {
            var s1 = new Channel();
            s1.channelName = "晚间新闻" + i;
            s1.channelPath = "night-news" + i;
            var save1 = s.add(s1);
            for (int j = 0; j < 10; j = j + 1) {
                var c1 = new Content();
                c1.content = "重大晚间新闻的内容<span style='color:red'>红色的文字</span>" + j;
                c1.contentTitle = "重大晚间新闻的标题👍" + j;
                c1.channelID = save1.id;
                c.add(c1);
            }
        }

        return "初始化成功 : " + yyyy_MM_dd_HH_mm_ss.format(LocalDateTime.now());
    }

}
