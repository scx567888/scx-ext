package cool.scx.ext.crud.exception;

import cool.scx.http.exception.impl.BadRequestException;
import cool.scx.vo.Json;

public final class UnknownWhereType extends BadRequestException {

    public UnknownWhereType(String fieldName, String strWhereType) {
        super(Json.fail("unknown-where-type").put("field-name", fieldName).put("where-type", strWhereType).toJson(""));
    }

}