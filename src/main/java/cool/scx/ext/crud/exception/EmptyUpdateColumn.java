package cool.scx.ext.crud.exception;

import cool.scx.http.exception.impl.BadRequestException;
import cool.scx.vo.Json;

/**
 * a
 */
public final class EmptyUpdateColumn extends BadRequestException {

    public EmptyUpdateColumn() {
        super(Json.fail("empty-update-column").toJson(""));
    }

}
