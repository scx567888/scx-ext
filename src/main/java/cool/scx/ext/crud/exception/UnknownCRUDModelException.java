package cool.scx.ext.crud.exception;

import cool.scx.http.exception.impl.BadRequestException;
import cool.scx.vo.Json;

public final class UnknownCRUDModelException extends BadRequestException {

    private final String modelName;

    public UnknownCRUDModelException(String modelName) {
        super(Json.fail("unknown-crud-model").put("model-name", modelName).toJson(""));
        this.modelName = modelName;
    }

    @Override
    public String getMessage() {
        return "未找到对应名称为 : [" + modelName + "] 的 BaseModelClass !!!";
    }

}
