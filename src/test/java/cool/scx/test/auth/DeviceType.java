package cool.scx.test.auth;

/**
 * 登录设备类型
 *
 * @author scx567888
 * @version 1.0.2
 */
public enum DeviceType {

    /**
     * 安卓设备
     */
    ANDROID,

    /**
     * 苹果设备
     */
    APPLE,

    /**
     * 后台管理
     */
    ADMIN,

    /**
     * 网页
     */
    WEBSITE,

    /**
     * 未知
     */
    UNKNOWN;

    /**
     * <p>of.</p>
     *
     * @param name a {@link String} object
     * @return a {@link DeviceType} object
     */
    public static DeviceType of(String name) {
        var upperCaseName = name.trim().toUpperCase();
        return switch (upperCaseName) {
            case "WEBSITE" -> WEBSITE;
            case "APPLE" -> APPLE;
            case "ADMIN" -> ADMIN;
            case "ANDROID" -> ANDROID;
            default -> UNKNOWN;
        };
    }

}