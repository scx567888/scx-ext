package cool.scx.ext.organization.auth;

/**
 * 权限标识
 */
public interface PermFlag {

    /**
     * 权限说明文字
     *
     * @return 权限说明文字
     */
    default String description() {
        return this.permString();
    }

    /**
     * 权限字符串
     *
     * @return 权限字符串
     */
    String permString();

}
