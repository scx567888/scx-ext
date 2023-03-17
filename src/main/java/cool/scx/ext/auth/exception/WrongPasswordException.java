package cool.scx.ext.auth.exception;

/**
 * 密码错误异常
 *
 * @author scx567888
 * @version 0.3.6
 */
public final class WrongPasswordException extends AuthException {

    private Long userID;

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

    public WrongPasswordException(Long userID) {
        this.userID = userID;
    }

    public Long userID() {
        return userID;
    }

}
