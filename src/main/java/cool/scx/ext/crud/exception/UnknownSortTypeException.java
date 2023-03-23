package cool.scx.ext.crud.exception;

import cool.scx.mvc.exception.BadRequestException;
import cool.scx.mvc.vo.Data;

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
        super(Data.fail("unknown-sort-type").put("field-name", fieldName).put("sort-type", strSortType).toJson(""));
    }

}
