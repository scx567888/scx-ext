package cool.scx.test.auth.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限注解
 *
 * @author scx567888
 * @version 1.3.14
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Perms {

    /**
     * 不校验权限
     *
     * @return 权限
     */
    boolean checkedPerms() default true;

    /**
     * 检查登录
     *
     * @return 检查登录的类型
     */
    boolean checkedLogin() default true;

}