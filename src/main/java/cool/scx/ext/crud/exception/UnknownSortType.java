package cool.scx.ext.crud.exception;

import cool.scx.http.exception.impl.BadRequestException;
import cool.scx.vo.Json;

public final class UnknownSortType extends BadRequestException {

    public UnknownSortType(String fieldName, String strSortType) {
        super(Json.fail("unknown-sort-type").put("field-name", fieldName).put("sort-type", strSortType).toJson(""));
    }

}
