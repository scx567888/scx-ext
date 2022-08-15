package cool.scx.ext.organization.base;

/**
 * <p>AccountService class.</p>
 *
 * @author scx567888
 * @version 1.11.8
 */
public abstract class BaseAccountService<T extends BaseUser> extends UserInfoModelService<T, BaseAccount<T>> {

    /**
     * <p>Constructor for AccountService.</p>
     *
     * @param userService a {@link BaseUserService} object
     */
    protected BaseAccountService(BaseUserService<T> userService) {
        super(userService);
    }

}
