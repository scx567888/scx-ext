package cool.scx.ext.cms.test.website;

import cool.scx.core.ScxConstant;
import cool.scx.core.ScxContext;
import cool.scx.core.annotation.FromQuery;
import cool.scx.core.annotation.ScxMapping;
import cool.scx.core.enumeration.HttpMethod;
import cool.scx.core.enumeration.RawType;
import cool.scx.core.vo.BaseVo;
import cool.scx.core.vo.Download;
import cool.scx.core.vo.Html;
import cool.scx.core.vo.Raw;
import cool.scx.ext.cms.channel.Channel;
import cool.scx.ext.cms.channel.ChannelService;
import cool.scx.ext.cms.content.Content;
import cool.scx.ext.cms.content.ContentService;

import cool.scx.ext.cms.test.auth.TestUser;
import cool.scx.ext.cms.test.auth.TestUserService;
import cool.scx.util.CryptoUtils;
import cool.scx.util.RandomUtils;
import cool.scx.util.StringUtils;
import cool.scx.util.ZipBuilder;
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
        var bean = ScxContext.getBean(TestUserService.class);
        try {
            ScxContext.sqlRunner().autoTransaction(() -> {
                sb.append("事务开始前数据库中 数据条数 : ").append(bean.list().size()).append("</br>");
                sb.append("现在插入 1 数据条数").append("</br>");
                var u = new TestUser();
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
