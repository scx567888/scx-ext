package cool.scx.ext.crud.exception;

import cool.scx.mvc.exception.BadRequestException;
import cool.scx.mvc.vo.Result;

/**
 * a
 *
 * @author scx567888
 * @version 1.7.7
 */
public final class UnknownWhereTypeException extends BadRequestException {

    /**
     * a
     *
     * @param fieldName    a
     * @param strWhereType a
     */
    public UnknownWhereTypeException(String fieldName, String strWhereType) {
        super(Result.fail("unknown-where-type").put("field-name", fieldName).put("where-type", strWhereType).toJson(""));
    }

}
