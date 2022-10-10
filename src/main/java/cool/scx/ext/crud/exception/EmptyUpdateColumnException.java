package cool.scx.ext.crud.exception;

import cool.scx.core.http.exception.BadRequestException;
import cool.scx.core.vo.Json;

/**
 * a
 *
 * @author scx567888
 * @version 1.10.8
 */
public final class EmptyUpdateColumnException extends BadRequestException {

    /**
     * <p>Constructor for EmptyUpdateColumnException.</p>
     */
    public EmptyUpdateColumnException() {
        super(Json.fail("empty-update-column").toJson(""));
    }

}
