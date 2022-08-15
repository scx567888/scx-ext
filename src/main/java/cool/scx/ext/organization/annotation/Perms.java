package cool.scx.ext.organization.annotation;

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
     * 是否校验权限
     *
     * @return 是否
     */
    boolean checkPerms() default true;

    /**
     * 是否检查登录
     *
     * @return 是否
     */
    boolean checkLogin() default true;

}
