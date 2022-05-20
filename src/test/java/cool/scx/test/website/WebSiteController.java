package cool.scx.test.website;

import cool.scx.ScxConstant;
import cool.scx.ScxContext;
import cool.scx.annotation.FromQuery;
import cool.scx.annotation.ScxMapping;
import cool.scx.enumeration.HttpMethod;
import cool.scx.enumeration.RawType;
import cool.scx.ext.cms.channel.Channel;
import cool.scx.ext.cms.channel.ChannelService;
import cool.scx.ext.cms.content.Content;
import cool.scx.ext.cms.content.ContentService;
import cool.scx.ext.util.Excel;
import cool.scx.ext.util.QRCodeUtils;
import cool.scx.test.user.User;
import cool.scx.test.user.UserService;
import cool.scx.util.CryptoUtils;
import cool.scx.util.RandomUtils;
import cool.scx.util.StringUtils;
import cool.scx.util.zip.VirtualDirectory;
import cool.scx.util.zip.VirtualFile;
import cool.scx.util.zip.ZipAction;
import cool.scx.vo.BaseVo;
import cool.scx.vo.Download;
import cool.scx.vo.Html;
import cool.scx.vo.Raw;
import io.vertx.ext.web.RoutingContext;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * 简单测试
 *
 * @author scx567888
 * @version 0.3.6
 * @since 1.3.14
 */
@ScxMapping("/")
public class WebSiteController {

    @ScxMapping(method = HttpMethod.GET)
    public static void TestTransaction(RoutingContext ctx) throws Exception {
        var sb = new StringBuilder();
        UserService bean = ScxContext.getBean(UserService.class);
        try {
            ScxContext.sqlRunner().autoTransaction(() -> {
                sb.append("事务开始前数据库中 数据条数 : ").append(bean.list().size()).append("</br>");
                sb.append("现在插入 1 数据条数").append("</br>");
                var u = new User();
                u.password = CryptoUtils.encryptPassword("123456");
                u.username = "唯一name";
                bean.add(u);
                sb.append("现在数据库中数据条数 : ").append(bean.list().size()).append("</br>");
                bean.add(u);
            });
        } catch (Exception e) {
            sb.append("出错了 后滚后数据库中数据条数 : ").append(bean.list().size());
        }
        Html.ofString(sb.toString()).handle(ctx);
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

        return new Download(excel.toBytes(), "测试 Excel 😎👀😃✨😜.xlsx");
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
            value = RandomUtils.getUUID() + " : 前面的是UUID";
        }
        //这里返回的是一个 png 的 图片 byte 数组
        byte[] qrCode = QRCodeUtils.getQRCode(value, 300);

        return new Raw(qrCode, RawType.PNG);
    }

    /**
     * <p>qrcode.</p>
     *
     * @return a {@link BaseVo} object
     */
    @ScxMapping(method = HttpMethod.GET)
    public BaseVo zip() throws Exception {
        var virtualDirectory = VirtualDirectory.of("第一个目录");
        virtualDirectory.put("第二个目录", VirtualFile.of("第二个目录中的文件.txt", "文件内容".getBytes(StandardCharsets.UTF_8)));
        virtualDirectory.getOrCreate("这是一系列空目录/这是一系列空目录/这是一系列空目录/这是一系列空目录/这是一系列空目录");
        var orCreate = virtualDirectory.getOrCreate("这不是一系列空目录/这不是一系列空目录/这不是一系列空目录/这不是一系列空目录/这不是一系列空目录");
        if (orCreate instanceof VirtualDirectory a) {
            a.put(VirtualFile.of("一个二维码图片.png", QRCodeUtils.getQRCode("一个二维码图片", 300)));
        }
        byte[] bytes = ZipAction.toZipFileByteArray(virtualDirectory);
        return new Download(bytes, "测试压缩包.zip");
    }

    @ScxMapping(method = HttpMethod.GET)
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

        return "初始化成功 : " + ScxConstant.DEFAULT_DATETIME_FORMATTER.format(LocalDateTime.now());
    }

}
