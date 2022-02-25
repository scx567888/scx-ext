package cool.scx.ext.crud.exception;

import cool.scx.http.exception.impl.BadRequestException;
import cool.scx.sql.where.WhereType;
import cool.scx.vo.Json;

public final class WhereBodyParametersSizeError extends BadRequestException {

    public WhereBodyParametersSizeError(String fieldName, WhereType whereType, int gotParametersSize) {
        super(Json.fail("where-body-parameters-size-error")
                .put("field-name", fieldName)
                .put("where-type", whereType)
                .put("need-parameters-size", whereType.paramSize())
                .put("got-parameters-size", gotParametersSize)
                .toJson(""));

    }
}
