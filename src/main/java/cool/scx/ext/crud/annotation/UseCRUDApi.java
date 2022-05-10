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
     */
    boolean list = true;

    /**
     * info
     */
    boolean info = true;

    /**
     * save
     */
    boolean save = true;

    /**
     * update
     */
    boolean update = true;

    /**
     * delete
     */
    boolean delete = true;

    /**
     * batchDelete
     */
    boolean batchDelete = true;

    /**
     * revokeDelete
     */
    boolean revokeDelete = true;

    /**
     * checkUnique
     */
    boolean checkUnique = true;

}
