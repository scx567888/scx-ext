package cool.scx.ext.auth.exception;

import cool.scx.common.util.CaseUtils;
import cool.scx.mvc.exception.BadRequestException;
import cool.scx.mvc.vo.BaseVo;
import cool.scx.mvc.vo.Result;

/**
 * 认证异常父类
 *
 * @author scx567888
 * @version 0.3.6
 */
public abstract class AuthException extends BadRequestException {

    /**
     * <p>Constructor for AuthException.</p>
     *
     * @param e a {@link java.lang.Exception} object
     */
    public AuthException(Exception e) {
        super(e);
    }

    /**
     * <p>Constructor for AuthException.</p>
     */
    public AuthException() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        return "AuthException";
    }

    /**
     * 方便将异常转换为 BaseVo 以便前台识别
     *
     * @return baseVo
     */
    public BaseVo toBaseVo() {
        var simpleName = this.getClass().getSimpleName();
        var message = simpleName.replaceAll("Exception", "");
        return Result.fail(CaseUtils.toKebab(message));
    }

}
