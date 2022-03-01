package cool.scx.ext.crud.exception;

import cool.scx.http.exception.impl.BadRequestException;
import cool.scx.vo.Json;

/**
 * a
 *
 * @author scx567888
 * @version 1.7.7
 */
public final class UnknownSortType extends BadRequestException {

    /**
     * a
     *
     * @param fieldName   a
     * @param strSortType a
     */
    public UnknownSortType(String fieldName, String strSortType) {
        super(Json.fail("unknown-sort-type").put("field-name", fieldName).put("sort-type", strSortType).toJson(""));
    }

}
