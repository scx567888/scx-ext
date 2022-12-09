package cool.scx.ext.crud.exception;

import cool.scx.core.http.exception.BadRequestException;
import cool.scx.core.vo.Json;

/**
 * a
 *
 * @author scx567888
 * @version 1.7.7
 */
public final class UnknownSortTypeException extends BadRequestException {

    /**
     * a
     *
     * @param fieldName   a
     * @param strSortType a
     */
    public UnknownSortTypeException(String fieldName, String strSortType) {
        super(Json.fail("unknown-sort-type").put("field-name", fieldName).put("sort-type", strSortType).toJson(""));
    }

}
