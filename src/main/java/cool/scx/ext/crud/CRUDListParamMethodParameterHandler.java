package cool.scx.ext.crud;

import cool.scx.mvc.parameter_handler.ScxMappingMethodParameterHandler;
import cool.scx.mvc.parameter_handler.ScxMappingRoutingContextInfo;
import cool.scx.mvc.parameter_handler.impl.FromBodyMethodParameterHandler;

import java.lang.reflect.Parameter;

/**
 * a
 */
public final class CRUDListParamMethodParameterHandler implements ScxMappingMethodParameterHandler {

    /**
     * a
     */
    public static final CRUDListParamMethodParameterHandler DEFAULT_INSTANCE = new CRUDListParamMethodParameterHandler();

    @Override
    public boolean canHandle(Parameter parameter) {
        return parameter.getParameterizedType() == CRUDListParam.class;
    }

    @Override
    public Object handle(Parameter parameter, ScxMappingRoutingContextInfo scxMappingRoutingContextInfo) throws Exception {
        var javaType = parameter.getParameterizedType();
        var name = parameter.getName();
        var required = false;
        var useAllBody = true;
        var crudListParam = FromBodyMethodParameterHandler.getValueFromBody(name, useAllBody, required, javaType, scxMappingRoutingContextInfo);
        //这里保证 方法上的 CRUDListParam 类型参数永远不为空
        return crudListParam != null ? crudListParam : new CRUDListParam();
    }

}
