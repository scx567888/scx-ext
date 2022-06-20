package cool.scx.ext.organization.exception;

import cool.scx.vo.BaseVo;
import cool.scx.vo.Json;

/**
 * 未知设备异常
 * <p>
 * 为了区分请求的来源以判断获取 token 的方式<p>
 * 需要在请求头 (header) 中设置 S-Device 字段标识 如果没有则会抛出这个异常
 *
 * @author scx567888
 * @version 1.1.5
 */
public final class UnknownDeviceException extends AuthException {

    /**
     * {@inheritDoc}
     */
    @Override
    public BaseVo toBaseVo() {
        return Json.fail("unknown-device");
    }

}
