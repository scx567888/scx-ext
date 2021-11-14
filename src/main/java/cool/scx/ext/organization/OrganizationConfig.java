package cool.scx.ext.organization;

import cool.scx.ScxContext;
import cool.scx.util.ansi.Ansi;

/**
 * 核心模块配置文件
 *
 * @author scx567888
 * @version 1.1.2
 */
public class OrganizationConfig {

    private static final boolean confusionLoginError;

    static {
        confusionLoginError = ScxContext.config().getOrDefault("organization.confusion-login-error", false);
        Ansi.out().magenta("Y 是否混淆登录错误                     \t -->\t " + (confusionLoginError ? "是" : "否")).println();
    }

    /**
     * 初始化方法
     */
    public static void initConfig() {

    }

    /**
     * 混淆登录错误 (是否将 "用户未找到" 和 "密码错误" 统称为 "用户名或密码错误" )
     * 防止恶意攻击暴力破解
     *
     * @return b
     */
    public static boolean confusionLoginError() {
        return confusionLoginError;
    }

}
