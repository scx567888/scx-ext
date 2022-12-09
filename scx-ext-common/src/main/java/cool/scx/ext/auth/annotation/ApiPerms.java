package cool.scx.ext.auth.annotation;

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
public @interface ApiPerms {

    /**
     * 权限字符串值 当为空(默认)时会以 类名称 + ":" 方法名 为值
     *
     * @return 权限字符串值
     */
    String value() default "";

    /**
     * 是否校验权限
     *
     * @return 是否
     */
    boolean checkPerms() default true;

}
