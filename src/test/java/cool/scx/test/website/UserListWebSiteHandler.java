package cool.scx.test.website;

import cool.scx.annotation.ScxService;
import cool.scx.base.Query;
import cool.scx.ext.cms.web_site.WebSiteHandler;
import cool.scx.test.auth.TestAuth;
import cool.scx.test.user.User;
import cool.scx.test.user.UserService;
import cool.scx.util.CryptoUtils;
import cool.scx.util.RandomUtils;
import cool.scx.vo.Html;

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
            for (int i = 0; i < 25; i++) {
                var s = new User();
                var uuid = RandomUtils.getUUID();
                //测试表情符能否存储
                s.username = uuid + "👶";
                s.nickname = uuid + "🥝";
                s.password = CryptoUtils.encryptPassword(uuid);
                s.isAdmin = true;
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
        html.add("loginUser", TestAuth.getLoginUser());
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
