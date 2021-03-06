package cool.scx.ext.crud.exception;

import cool.scx.core.http.exception.impl.NotFoundException;
import cool.scx.core.vo.Json;
import cool.scx.ext.crud.CRUDApiType;

/**
 * a
 *
 * @author scx567888
 * @version 1.10.8
 */
public final class CRUDApiAlreadyDisableException extends NotFoundException {

    /**
     * a
     */
    private final String modelName;

    /**
     * a
     */
    private final CRUDApiType apiType;

    /**
     * a
     *
     * @param modelName a
     * @param apiType   a {@link cool.scx.ext.crud.CRUDApiType} object
     */
    public CRUDApiAlreadyDisableException(String modelName, CRUDApiType apiType) {
        super(Json.fail("crud-api-already-disable").put("model-name", modelName).put("api-name", apiType.name()).toJson(""));
        this.modelName = modelName;
        this.apiType = apiType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        return "对应名称为 : [" + modelName + "] 的 BaseModel 并未启用类型为 [" + apiType.name() + "] 的 API !!!";
    }

}
