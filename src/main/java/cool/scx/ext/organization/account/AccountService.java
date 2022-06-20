package cool.scx.ext.organization.account;

import cool.scx.annotation.ScxService;
import cool.scx.ext.organization.auth.UserInfoModelService;
import cool.scx.ext.organization.user.UserService;

/**
 * <p>AccountService class.</p>
 *
 * @author scx567888
 * @version 1.11.8
 */
@ScxService
public class AccountService extends UserInfoModelService<Account> {

    /**
     * <p>Constructor for AccountService.</p>
     *
     * @param userService a {@link cool.scx.ext.organization.user.UserService} object
     */
    protected AccountService(UserService userService) {
        super(userService);
    }

}
