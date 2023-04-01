package cool.scx.ext.crud.exception;

import cool.scx.dao.ColumnFilter;
import cool.scx.mvc.exception.BadRequestException;
import cool.scx.mvc.vo.Result;

/**
 * a
 *
 * @author scx567888
 * @version 1.10.8
 */
public final class EmptySelectColumnException extends BadRequestException {

    /**
     * a
     *
     * @param filterMode a
     * @param fieldNames a
     */
    public EmptySelectColumnException(ColumnFilter.FilterMode filterMode, String[] fieldNames) {
        super(Result.fail("empty-select-column").put("filter-mode", filterMode).put("field-names", fieldNames).toJson(""));
    }

}
