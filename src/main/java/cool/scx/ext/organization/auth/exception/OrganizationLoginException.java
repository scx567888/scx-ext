package cool.scx.ext.organization.auth.exception;

/**
 * 认证异常
 *
 * @author scx567888
 * @version 0.3.6
 */
public abstract class OrganizationLoginException extends Exception {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        return "OrganizationLoginException";
    }

}
