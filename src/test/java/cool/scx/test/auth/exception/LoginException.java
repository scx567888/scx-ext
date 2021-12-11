package cool.scx.test.auth.exception;

/**
 * 认证异常
 *
 * @author scx567888
 * @version 0.3.6
 */
public abstract class LoginException extends RuntimeException {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        return "LoginException";
    }

}
