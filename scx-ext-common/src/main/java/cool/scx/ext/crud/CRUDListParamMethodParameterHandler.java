package cool.scx.ext.crud;

import cool.scx.core.mvc.ScxMappingMethodParameterHandler;
import cool.scx.core.mvc.ScxMappingRoutingContextInfo;

import java.lang.reflect.Parameter;

import static cool.scx.core.mvc.parameter_handler.FromBodyMethodParameterHandler.getValueFromBody;
import static cool.scx.util.ObjectUtils.constructType;

/**
 * a
 *
 * @author scx567888
 * @version 1.10.8
 */
public final class CRUDListParamMethodParameterHandler implements ScxMappingMethodParameterHandler {

    /**
     * a
     */
    public static final CRUDListParamMethodParameterHandler DEFAULT_INSTANCE = new CRUDListParamMethodParameterHandler();

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canHandle(Parameter parameter) {
        return parameter.getParameterizedType() == CRUDListParam.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object handle(Parameter parameter, ScxMappingRoutingContextInfo info) throws Exception {
        var javaType = constructType(parameter.getParameterizedType());
        var name = parameter.getName();
        var required = false;
        var useAllBody = true;
        var crudListParam = getValueFromBody(name, useAllBody, required, javaType, info);
        //这里保证 方法上的 CRUDListParam 类型参数永远不为空
        return crudListParam != null ? crudListParam : new CRUDListParam();
    }

}
