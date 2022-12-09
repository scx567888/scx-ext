package cool.scx.ext.auth.test.website;

import cool.scx.core.ScxContext;
import cool.scx.core.annotation.ScxMapping;
import cool.scx.core.enumeration.HttpMethod;
import cool.scx.core.vo.Html;

import cool.scx.ext.auth.test.auth.TestContext;
import cool.scx.ext.auth.test.auth.TestUser;
import cool.scx.ext.auth.test.auth.TestUserService;
import cool.scx.sql.base.Query;
import cool.scx.util.CryptoUtils;
import cool.scx.util.RandomUtils;
import io.vertx.ext.web.RoutingContext;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 简单测试
 *
 * @author scx567888
 * @version 0.3.6
 * @since 1.3.14
 */
@ScxMapping("/")
public class WebSiteController {

    private final TestUserService userService;

    public WebSiteController(TestUserService userService) {
        this.userService = userService;
    }

    @ScxMapping(value = "/",method = HttpMethod.GET)
    public Html index() throws IOException {
        var html= Html.of("index");
        long count = userService.count(new Query());
        if (count < 50) {
            var s1 = new ArrayList<TestUser>();
            for (int i = 0; i < 25; i = i + 1) {
                var s = new TestUser();
                var uuid = RandomUtils.randomUUID();
                //测试表情符能否存储
                s.username = uuid + "👶";
                s.phoneNumber = uuid + "🥝";
                s.password = CryptoUtils.encryptPassword(uuid);
                s.isAdmin = true;
                s1.add(s);
            }
            userService.add(s1);
            for (int i = 0; i < 25; i = i + 1) {
                var s = new TestUser();
                var uuid = RandomUtils.randomUUID();
                s.username = uuid;
                s.phoneNumber = uuid;
                s.password = CryptoUtils.encryptPassword(uuid);
                userService.add(s);
            }
        }
        var users = userService.list(new Query().setPagination(100));
        html.add("userList", users);
        html.add("name", "小明");
        html.add("age", 22);
        return html.add("loginUser", TestContext.getCurrentUser());
    }

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

}
