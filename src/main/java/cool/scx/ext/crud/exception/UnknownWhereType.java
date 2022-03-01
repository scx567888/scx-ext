package cool.scx.ext.crud.exception;

import cool.scx.http.exception.impl.BadRequestException;
import cool.scx.vo.Json;

/**
 * a
 *
 * @author scx567888
 * @version 1.7.7
 */
public final class UnknownWhereType extends BadRequestException {

    /**
     * a
     *
     * @param fieldName    a
     * @param strWhereType a
     */
    public UnknownWhereType(String fieldName, String strWhereType) {
        super(Json.fail("unknown-where-type").put("field-name", fieldName).put("where-type", strWhereType).toJson(""));
    }

}
