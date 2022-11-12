package cool.scx.ext.crud.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 此注解需标注在 实体类上
 * <p>
 * 若标注此注解的实体类将 可以 通过 CRUDModule 通用的 api 进行操作
 *
 * @author scx567888
 * @version 1.7.7
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UseCRUDApi {

    /**
     * list
     *
     * @return a boolean
     */
    boolean list() default true;

    /**
     * info
     *
     * @return a boolean
     */
    boolean info() default true;

    /**
     * save
     *
     * @return a boolean
     */
    boolean add() default true;

    /**
     * update
     *
     * @return a boolean
     */
    boolean update() default true;

    /**
     * delete
     *
     * @return a boolean
     */
    boolean delete() default true;

    /**
     * batchDelete
     *
     * @return a boolean
     */
    boolean batchDelete() default true;

    /**
     * checkUnique
     *
     * @return a boolean
     */
    boolean checkUnique() default true;

}
