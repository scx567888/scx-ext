package cool.scx.test.auth;

import cool.scx.ext.auth.BaseAuthApi;
import cool.scx.ext.auth.BaseAuthHandler;
import cool.scx.mvc.annotation.ScxRoute;

/**
 * 默认认证 api 推荐使用
 * 也可以不用此 api 但需要将 自定义 AuthHandler 的实现中的方法清空
 *
 * @author scx567888
 * @version 0.3.6
 */
@ScxRoute("/api/auth")
public class TestAuthApi extends BaseAuthApi<TestUser> {

    /**
     * a
     *
     * @param authHandler a
     * @param userService a
     */
    protected TestAuthApi(BaseAuthHandler<TestUser> authHandler, TestUserService userService) {
        super(authHandler, userService);
    }

}
