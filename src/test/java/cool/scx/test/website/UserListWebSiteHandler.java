package cool.scx.test.website;

import cool.scx.core.annotation.ScxService;
import cool.scx.core.base.Query;
import cool.scx.core.vo.Html;
import cool.scx.ext.cms.web_site.WebSiteHandler;
import cool.scx.ext.organization.auth.ScxAuth;
import cool.scx.ext.organization.user.User;
import cool.scx.ext.organization.user.UserService;
import cool.scx.util.CryptoUtils;
import cool.scx.util.RandomUtils;

import java.util.ArrayList;


/**
 * <p>TestUserListWebSiteHandler class.</p>
 *
 * @author scx567888
 * @version 1.3.14
 * @since 1.3.14
 */
@ScxService
public class UserListWebSiteHandler implements WebSiteHandler {

    private final UserService userService;

    /**
     * <p>Constructor for TestUserListWebSiteHandler.</p>
     *
     * @param userService a {@link UserService} object
     */
    public UserListWebSiteHandler(UserService userService) {
        this.userService = userService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void indexHandler(Html html) {
        long count = userService.count(new Query());
        if (count < 50) {
            var s1 = new ArrayList<User>();
            for (int i = 0; i < 25; i = i + 1) {
                var s = new User();
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
                var s = new User();
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
        html.add("loginUser", ScxAuth.getLoginUser());
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
