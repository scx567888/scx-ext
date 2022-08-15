package cool.scx.ext.organization._impl;

import cool.scx.core.annotation.ScxMapping;
import cool.scx.ext.organization.base.BaseAuthController;

/**
 * 默认认证 api 推荐使用
 * 也可以不用此 api 但需要将 自定义 AuthHandler 的实现中的方法清空
 *
 * @author scx567888
 * @version 0.3.6
 */
@ScxMapping("/api/auth")
public class AuthController extends BaseAuthController<User> {

    /**
     * <p>Constructor for ScxAuthController.</p>
     *
     * @param userService a
     */
    public AuthController(UserService userService) {
        super(userService);
    }

}
