package cool.scx.ext.organization.base;

/**
 * <p>AccountService class.</p>
 *
 * @author scx567888
 * @version 1.11.8
 */
public abstract class BaseAccountService<T extends BaseAccount<?>> extends UserInfoModelService<T> {

    /**
     * <p>Constructor for AccountService.</p>
     *
     * @param userService a {@link cool.scx.ext.organization.base.BaseUserService} object
     */
    protected BaseAccountService(BaseUserService<?> userService) {
        super(userService);
    }

}
