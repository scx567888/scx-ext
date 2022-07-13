package cool.scx.ext.organization.exception;

import cool.scx.core.vo.BaseVo;
import cool.scx.core.vo.Json;

/**
 * 未知 登录 Handler
 *
 * @author scx567888
 * @version 1.11.8
 */
public final class UnknownLoginHandlerException extends AuthException {

    /**
     * {@inheritDoc}
     */
    @Override
    public BaseVo toBaseVo() {
        return Json.fail("unknown-login-handler");
    }
}
