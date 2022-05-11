package cool.scx.ext.crud.exception;

import cool.scx.http.exception.impl.BadRequestException;
import cool.scx.vo.Json;

/**
 * a
 *
 * @author scx567888
 * @version 1.8.1
 */
public final class UnknownFilterModeException extends BadRequestException {

    /**
     * a
     *
     * @param filterMode a
     */
    public UnknownFilterModeException(String filterMode) {
        super(Json.fail("unknown-filter-mode").put("filter-mode", filterMode).toJson(""));
    }

}
