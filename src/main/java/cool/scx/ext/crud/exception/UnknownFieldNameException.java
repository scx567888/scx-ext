package cool.scx.ext.crud.exception;

import cool.scx.core.http.exception.impl.BadRequestException;
import cool.scx.core.vo.Json;

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
        super(Json.fail("unknown-field-name").put("field-name", fieldName).toJson(""));
    }

}
