package cool.scx.ext.organization.exception;

/**
 * 认证异常
 *
 * @author scx567888
 * @version 0.3.6
 */
public abstract class OrganizationLoginException extends RuntimeException {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        return "OrganizationLoginException";
    }

}
