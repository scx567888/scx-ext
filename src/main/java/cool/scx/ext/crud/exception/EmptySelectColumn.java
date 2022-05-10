package cool.scx.ext.crud.exception;

import cool.scx.base.AbstractFilter;
import cool.scx.http.exception.impl.BadRequestException;
import cool.scx.vo.Json;

/**
 * a
 */
public final class EmptySelectColumn extends BadRequestException {

    /**
     * a
     *
     * @param filterMode a
     * @param fieldNames a
     */
    public EmptySelectColumn(AbstractFilter.FilterMode filterMode, String[] fieldNames) {
        super(Json.fail("empty-select-column").put("filter-mode", filterMode).put("field-names", fieldNames).toJson(""));
    }

}
