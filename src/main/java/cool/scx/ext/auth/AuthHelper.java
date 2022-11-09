package cool.scx.ext.auth;

import cool.scx.ext.auth.exception.AuthException;
import cool.scx.ext.auth.exception.UnknownDeviceException;
import cool.scx.util.CryptoUtils;
import cool.scx.util.RandomUtils;
import io.vertx.ext.web.RoutingContext;

import static cool.scx.ext.auth.BaseAuthHandler.SCX_AUTH_DEVICE_KEY;
import static cool.scx.ext.auth.BaseAuthHandler.SCX_AUTH_TOKEN_KEY;

/**
 * <p>AuthHelper class.</p>
 *
 * @author scx567888
 * @version 1.15.4
 */
public final class AuthHelper {

    /**
     * 根据 设备类型自行判断 获取 token
     *
     * @param ctx a {@link io.vertx.ext.web.RoutingContext} object
     * @return a {@link java.lang.String} object
     */
    public static String getToken(RoutingContext ctx) {
        var device = getDeviceTypeByHeader(ctx);
        return switch (device) {
            case WEBSITE -> getTokenFromCookie(ctx);
            case ADMIN, APPLE, ANDROID -> getTokenFromHeader(ctx);
            default -> null;
        };
    }

    /**
     * 获取用户的设备
     *
     * @param routingContext a {@link io.vertx.ext.web.RoutingContext} object
     * @return a
     */
    public static DeviceType getDeviceTypeByHeader(RoutingContext routingContext) {
        var device = routingContext.request().getHeader(SCX_AUTH_DEVICE_KEY);
        if (device == null) {
            return DeviceType.WEBSITE;
        }
        return DeviceType.of(device);
    }

    /**
     * 根据 cookie 获取 token
     *
     * @param routingContext a {@link io.vertx.ext.web.RoutingContext} object
     * @return a {@link java.lang.String} object
     */
    public static String getTokenFromCookie(RoutingContext routingContext) {
        var cookie = routingContext.request().getCookie(SCX_AUTH_TOKEN_KEY);
        return cookie != null ? cookie.getValue() : null;
    }

    /**
     * 根据 Header 获取 token
     *
     * @param routingContext a
     * @return a a
     */
    public static String getTokenFromHeader(RoutingContext routingContext) {
        return routingContext.request().getHeader(SCX_AUTH_TOKEN_KEY);
    }

    /**
     * 尝试获取一个可以作为认证的 Token 具体获取方式由设备类型决定
     *
     * @param ctx         a {@link io.vertx.ext.web.RoutingContext} object
     * @param loginDevice a {@link DeviceType} object
     * @return a {@link java.lang.String} object
     * @throws cool.scx.ext.auth.exception.AuthException if any.
     */
    public static String tryGetAuthToken(RoutingContext ctx, DeviceType loginDevice) throws AuthException {
        //查看登录的设备以判断如何获取 token
        return switch (loginDevice) {
            case ADMIN, ANDROID, APPLE ->
                //这些设备的 token 是保存在 header 中的 所以我们新生成一个 随机id 并将其返回到前台 , 并由前台通过 js 保存到浏览器中
                    RandomUtils.randomUUID();
            case WEBSITE ->
                //这里就是直接通过网页访问 这种情况是没法获取到自定义 header 的所以我们将 cookie 中随机颁发的 token 当作为唯一标识
                    getTokenFromCookie(ctx);
            case UNKNOWN ->
                //这里就不知道 设备类型了 我们直接抛出一个异常
                    throw new UnknownDeviceException();
        };
    }

    /**
     * 校验密码
     *
     * @param plainPassword     a
     * @param encryptedPassword a
     * @return a
     */
    public static boolean checkPassword(String plainPassword, String encryptedPassword) {
        try {
            return CryptoUtils.checkPassword(plainPassword, encryptedPassword);
        } catch (Exception e) {
            return false;
        }
    }

}
