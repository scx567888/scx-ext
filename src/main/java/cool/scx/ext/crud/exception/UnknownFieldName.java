package cool.scx.ext.crud.exception;

import cool.scx.http.exception.impl.BadRequestException;
import cool.scx.vo.Json;

public final class UnknownFieldName extends BadRequestException {

    public UnknownFieldName(String fieldName) {
        super(Json.fail("unknown-field-name").put("field-name", fieldName).toJson(""));
    }

}
