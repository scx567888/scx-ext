package cool.scx.ext.organization.base.impl;

import cool.scx.core.annotation.ScxMapping;
import cool.scx.ext.organization.base.BaseUserController;
import cool.scx.ext.organization.base.BaseUserService;

/**
 * <p>UserController class.</p>
 *
 * @author scx567888
 * @version 1.13.5
 */
@ScxMapping("/api/user")
public class UserController extends BaseUserController<User> {

    /**
     * <p>Constructor for UserController.</p>
     *
     * @param userService a {@link cool.scx.ext.organization.base.BaseUserService} object
     */
    public UserController(BaseUserService<User> userService) {
        super(userService);
    }
}
