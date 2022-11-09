package cool.scx.test.auth;

import cool.scx.core.annotation.ScxMapping;
import cool.scx.ext.organization.auth.BaseAuthApi;
import cool.scx.ext.organization.auth.BaseAuthHandler;
import cool.scx.ext.organization.base.BaseUserService;

/**
 * 默认认证 api 推荐使用
 * 也可以不用此 api 但需要将 自定义 AuthHandler 的实现中的方法清空
 *
 * @author scx567888
 * @version 0.3.6
 */
@ScxMapping("/api/auth")
public class TestAuthApi extends BaseAuthApi<TestUser> {

    /**
     * a
     *
     * @param authHandler a
     * @param userService a
     */
    protected TestAuthApi(BaseAuthHandler<TestUser> authHandler, BaseUserService<TestUser> userService) {
        super(authHandler, userService);
    }

}
