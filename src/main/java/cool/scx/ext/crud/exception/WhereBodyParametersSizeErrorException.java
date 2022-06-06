package cool.scx.ext.crud.exception;

import cool.scx.http.exception.impl.BadRequestException;
import cool.scx.sql.where.WhereType;
import cool.scx.vo.Json;

/**
 * a
 *
 * @author scx567888
 * @version 1.7.7
 */
public final class WhereBodyParametersSizeErrorException extends BadRequestException {

    /**
     * a
     *
     * @param fieldName         a
     * @param whereType         a
     * @param gotParametersSize a
     */
    public WhereBodyParametersSizeErrorException(String fieldName, WhereType whereType, int gotParametersSize) {
        super(Json.fail("where-body-parameters-size-error")
                .put("field-name", fieldName)
                .put("where-type", whereType)
                .put("need-parameters-size", whereType.paramSize())
                .put("got-parameters-size", gotParametersSize)
                .toJson(""));

    }

}