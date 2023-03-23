package cool.scx.ext.crud.exception;

import cool.scx.mvc.exception.BadRequestException;
import cool.scx.mvc.vo.Data;

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
        super(Data.fail("empty-update-column").toJson(""));
    }

}
