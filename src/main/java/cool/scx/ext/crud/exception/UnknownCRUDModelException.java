package cool.scx.ext.crud.exception;

import cool.scx.mvc.exception.NotFoundException;
import cool.scx.mvc.vo.Result;

/**
 * a
 *
 * @author scx567888
 * @version 1.7.7
 */
public final class UnknownCRUDModelException extends NotFoundException {

    /**
     * a
     */
    private final String modelName;

    /**
     * a
     *
     * @param modelName a
     */
    public UnknownCRUDModelException(String modelName) {
        super(Result.fail("unknown-crud-model").put("model-name", modelName).toJson(""));
        this.modelName = modelName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        return "未找到对应名称为 : [" + modelName + "] 的 BaseModelClass !!!";
    }

}
