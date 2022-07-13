package cool.scx.ext.organization.exception;

import cool.scx.core.vo.BaseVo;
import cool.scx.core.vo.Json;

/**
 * 未知 (未找到) 用户异常
 *
 * @author scx567888
 * @version 0.3.6
 */
public final class UnknownUserException extends AuthException {

    /**
     * {@inheritDoc}
     */
    @Override
    public BaseVo toBaseVo() {
        return Json.fail("user-not-found");
    }

}
