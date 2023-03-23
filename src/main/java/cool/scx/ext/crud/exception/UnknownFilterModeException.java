package cool.scx.ext.crud.exception;

import cool.scx.mvc.exception.BadRequestException;
import cool.scx.mvc.vo.Data;

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
        super(Data.fail("unknown-filter-mode").put("filter-mode", filterMode).toJson(""));
    }

}
