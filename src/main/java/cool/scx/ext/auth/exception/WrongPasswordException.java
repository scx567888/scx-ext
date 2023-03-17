package cool.scx.ext.auth.exception;

import cool.scx.ext.auth.BaseUser;

/**
 * 密码错误异常
 *
 * @author scx567888
 * @version 0.3.6
 */
public final class WrongPasswordException extends AuthException {

    private BaseUser user;

    /**
     * <p>Constructor for WrongPasswordException.</p>
     *
     * @param e a {@link java.lang.Exception} object
     */
    public WrongPasswordException(Exception e) {
        super(e);
    }

    /**
     * <p>Constructor for WrongPasswordException.</p>
     */
    public WrongPasswordException() {
    }

    public WrongPasswordException(BaseUser loginUser) {
        this.user = loginUser;
    }

    public BaseUser user() {
        return user;
    }

}
