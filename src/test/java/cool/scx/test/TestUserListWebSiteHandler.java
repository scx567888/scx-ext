package cool.scx.test;

import cool.scx.annotation.ScxService;
import cool.scx.bo.Query;
import cool.scx.ext.cms.web_site.WebSiteHandler;
import cool.scx.ext.organization.User;
import cool.scx.ext.organization.UserService;
import cool.scx.ext.organization.auth.OrganizationAuth;
import cool.scx.util.CryptoUtils;
import cool.scx.util.RandomUtils;
import cool.scx.vo.Html;

import java.sql.SQLException;
import java.util.ArrayList;


/**
 * <p>TestUserListWebSiteHandler class.</p>
 *
 * @author scx567888
 * @version 1.3.14
 * @since 1.3.14
 */
@ScxService
public class TestUserListWebSiteHandler implements WebSiteHandler {

    private final UserService userService;

    /**
     * <p>Constructor for TestUserListWebSiteHandler.</p>
     *
     * @param userService a {@link cool.scx.ext.organization.UserService} object
     */
    public TestUserListWebSiteHandler(UserService userService) {
        this.userService = userService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void indexHandler(Html html) throws SQLException {
        long count = userService.count(new Query());
        if (count < 50) {
            var s1 = new ArrayList<User>();
            for (int i = 0; i < 25; i++) {
                var s = new User();
                var uuid = RandomUtils.getUUID();
                //测试表情符能否存储
                s.username = uuid + "👶";
                s.nickname = uuid + "🥝";
                s.password = CryptoUtils.encryptPassword(uuid);
                s.isAdmin = false;
                s1.add(s);
            }
            userService.save(s1);
            for (int i = 0; i < 25; i++) {
                var s = new User();
                var uuid = RandomUtils.getUUID();
                s.username = uuid;
                s.nickname = uuid;
                s.password = CryptoUtils.encryptPassword(uuid);
                userService.save(s);
            }
        }
        var users = userService.list(new Query().setPagination(100));
        html.add("userList", users);
        html.add("name", "小明");
        html.add("age", 22);
        html.add("loginUser", OrganizationAuth.getLoginUser());
    }

    @Override
    public void channelHandler(Html channelTemplate, String channelPath) throws Exception {
        WebSiteHandler.super.channelHandler(channelTemplate, channelPath);
    }

    @Override
    public void contentHandler(Html contentTemplate, String channelPath, Long contentID) throws Exception {
        WebSiteHandler.super.contentHandler(contentTemplate, channelPath, contentID);
    }
}
