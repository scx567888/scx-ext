package cool.scx.ext.organization.exception;

import cool.scx.core.http.exception.impl.BadRequestException;
import cool.scx.core.vo.BaseVo;
import cool.scx.core.vo.Json;
import cool.scx.util.CaseUtils;

/**
 * 认证异常父类
 *
 * @author scx567888
 * @version 0.3.6
 */
public abstract class AuthException extends BadRequestException {

    private final String defaultExceptionName = initDefaultExceptionName();

    private String initDefaultExceptionName() {
        var simpleName = this.getClass().getSimpleName();
        var name = simpleName.replaceAll("Exception", "");
        return CaseUtils.toKebab(name);
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
        return Json.fail(this.defaultExceptionName);
    }

}
