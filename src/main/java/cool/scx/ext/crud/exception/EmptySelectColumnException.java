package cool.scx.ext.crud.exception;

import cool.scx.core.http.exception.BadRequestException;
import cool.scx.core.vo.Json;
import cool.scx.sql.base.ColumnInfoFilter;

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
    public EmptySelectColumnException(ColumnInfoFilter.FilterMode filterMode, String[] fieldNames) {
        super(Json.fail("empty-select-column").put("filter-mode", filterMode).put("field-names", fieldNames).toJson(""));
    }

}
