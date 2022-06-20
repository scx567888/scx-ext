package cool.scx.ext.organization.exception;

import cool.scx.http.exception.impl.BadRequestException;
import cool.scx.vo.BaseVo;

/**
 * 认证异常父类
 *
 * @author scx567888
 * @version 0.3.6
 */
public abstract class AuthException extends BadRequestException {

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
    public abstract BaseVo toBaseVo();

}
