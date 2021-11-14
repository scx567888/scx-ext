package cool.scx.ext.organization.auth;

/**
 * 登录设备类型
 *
 * @author scx567888
 * @version 1.0.2
 */
enum OrganizationAuthDeviceType {
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
     * @param name a {@link java.lang.String} object
     * @return a {@link OrganizationAuthDeviceType} object
     */
    public static OrganizationAuthDeviceType of(String name) {
        var upperCaseName = name.toUpperCase();
        return switch (upperCaseName) {
            case "WEBSITE" -> WEBSITE;
            case "APPLE" -> APPLE;
            case "ADMIN" -> ADMIN;
            case "ANDROID" -> ANDROID;
            default -> UNKNOWN;
        };
    }

}