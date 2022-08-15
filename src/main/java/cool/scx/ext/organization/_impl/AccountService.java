package cool.scx.ext.organization._impl;

import cool.scx.core.annotation.ScxService;
import cool.scx.ext.organization.base.BaseAccountService;
import cool.scx.ext.organization.base.BaseUserService;

/**
 * <p>AccountService class.</p>
 *
 * @author scx567888
 * @version 1.11.8
 */
@ScxService
public final class AccountService extends BaseAccountService<User> {

    /**
     * <p>Constructor for AccountService.</p>
     *
     * @param userService a {@link BaseUserService} object
     */
    public AccountService(UserService userService) {
        super(userService);
    }

}
