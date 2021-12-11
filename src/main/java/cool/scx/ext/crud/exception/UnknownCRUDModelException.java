package cool.scx.ext.crud.exception;

import cool.scx.exception.ScxHttpException;
import cool.scx.vo.Json;
import io.vertx.ext.web.RoutingContext;

public final class UnknownCRUDModelException extends ScxHttpException {

    private final String modelName;

    public UnknownCRUDModelException(String modelName) {
        this.modelName = modelName;
    }

    @Override
    public void handle(RoutingContext ctx) {
        Json.fail("unknown-crud-model").put("model-name", modelName).handle(ctx);
    }

    @Override
    public String getMessage() {
        return "未找到对应名称为 : [" + modelName + "] 的 BaseModelClass !!!";
    }

}
