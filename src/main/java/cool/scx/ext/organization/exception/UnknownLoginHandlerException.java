package cool.scx.ext.organization.exception;

import cool.scx.vo.BaseVo;
import cool.scx.vo.Json;

/**
 * 未知 登录 Handler
 */
public final class UnknownLoginHandlerException extends AuthException {

    @Override
    public BaseVo toBaseVo() {
        return Json.fail("unknown-login-handler");
    }
}
