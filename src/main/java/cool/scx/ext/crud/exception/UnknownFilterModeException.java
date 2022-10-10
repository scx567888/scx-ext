package cool.scx.ext.crud.exception;

import cool.scx.core.http.exception.BadRequestException;
import cool.scx.core.vo.Json;

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
