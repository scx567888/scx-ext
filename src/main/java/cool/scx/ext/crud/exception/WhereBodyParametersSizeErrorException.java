package cool.scx.ext.crud.exception;

import cool.scx.dao.query.WhereType;
import cool.scx.mvc.exception.BadRequestException;
import cool.scx.mvc.vo.Data;

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
        super(Data.fail("where-body-parameters-size-error")
                .put("field-name", fieldName)
                .put("where-type", whereType)
                .put("need-parameters-size", whereType.paramSize())
                .put("got-parameters-size", gotParametersSize)
                .toJson(""));

    }

}
