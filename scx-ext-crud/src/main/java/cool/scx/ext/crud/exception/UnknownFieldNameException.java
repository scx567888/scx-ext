package cool.scx.ext.crud.exception;

import cool.scx.mvc.exception.BadRequestException;
import cool.scx.mvc.vo.Result;

/**
 * a
 *
 * @author scx567888
 * @version 1.7.7
 */
public final class UnknownFieldNameException extends BadRequestException {

    /**
     * a
     *
     * @param fieldName a
     */
    public UnknownFieldNameException(String fieldName) {
        super(Result.fail("unknown-field-name").put("field-name", fieldName).toJson(""));
    }

}
