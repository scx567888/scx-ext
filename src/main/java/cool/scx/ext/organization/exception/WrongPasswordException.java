package cool.scx.ext.organization.exception;

import cool.scx.vo.BaseVo;
import cool.scx.vo.Json;

/**
 * 密码错误异常
 *
 * @author scx567888
 * @version 0.3.6
 */
public final class WrongPasswordException extends AuthException {

    /**
     * {@inheritDoc}
     */
    @Override
    public BaseVo toBaseVo() {
        return Json.fail("password-error");
    }

}
