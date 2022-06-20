package cool.scx.ext.organization.account;

import cool.scx.annotation.ScxService;
import cool.scx.ext.organization.auth.UserInfoModelService;
import cool.scx.ext.organization.user.UserService;

@ScxService
public class AccountService extends UserInfoModelService<Account> {

    protected AccountService(UserService userService) {
        super(userService);
    }

}
