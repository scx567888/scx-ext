package cool.scx.ext.crud;

import cool.scx.mvc.parameter_handler.ScxMappingMethodParameterHandler;
import cool.scx.mvc.parameter_handler.ScxMappingRoutingContextInfo;
import cool.scx.mvc.parameter_handler.impl.FromBodyMethodParameterHandler;

import java.lang.reflect.Parameter;

/**
 * a
 *
 * @author scx567888
 * @version 1.10.8
 */
public final class CRUDUpdateParamMethodParameterHandler implements ScxMappingMethodParameterHandler {

    /**
     * a
     */
    public static final CRUDUpdateParamMethodParameterHandler DEFAULT_INSTANCE = new CRUDUpdateParamMethodParameterHandler();

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canHandle(Parameter parameter) {
        return parameter.getParameterizedType() == CRUDUpdateParam.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object handle(Parameter parameter, ScxMappingRoutingContextInfo scxMappingRoutingContextInfo) throws Exception {
        var javaType = parameter.getParameterizedType();
        var name = parameter.getName();
        var required = false;
        var useAllBody = true;
        var crudUpdateParam = FromBodyMethodParameterHandler.getValueFromBody(name, useAllBody, required, javaType, scxMappingRoutingContextInfo);
        //这里保证 方法上的 CRUDUpdateParam 类型参数永远不为空
        return crudUpdateParam != null ? crudUpdateParam : new CRUDUpdateParam();
    }

}
