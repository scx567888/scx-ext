package cool.scx.ext.crud;

import cool.scx.core.ScxContext;
import cool.scx.core.ScxModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 为 BaseModel 的实现类 提供一套简单的 "单表" 的增删改查 api
 *
 * @author scx567888
 * @version 1.3.0
 */
public class CRUDModule implements ScxModule {

    /**
     * Constant <code>logger</code>
     */
    private static final Logger logger = LoggerFactory.getLogger(CRUDModule.class);

    /**
     * 默认实现类
     */
    private Class<? extends CRUDHandler> crudHandlerClass = CRUDHandlerImpl.class;

    /**
     * <p>Constructor for CmsModule.</p>
     */
    public CRUDModule() {

    }

    /**
     * <p>Constructor for CRUDModule.</p>
     *
     * @param c c
     */
    public CRUDModule(Class<? extends CRUDHandler> c) {
        crudHandlerClass = c;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        //这里添加额外的参数处理器 保证 CRUDListParam 类型的参数永不为空
        ScxContext.scxMappingConfiguration().addMethodParameterHandler(0, CRUDListParamMethodParameterHandler.DEFAULT_INSTANCE);
        ScxContext.scxMappingConfiguration().addMethodParameterHandler(0, CRUDUpdateParamMethodParameterHandler.DEFAULT_INSTANCE);
        logger.info("已添加用于处理类型为 CRUDListParam   的 MethodParameterHandler  -->  {}", CRUDListParamMethodParameterHandler.class.getName());
        logger.info("已添加用于处理类型为 CRUDUpdateParam 的 MethodParameterHandler  -->  {}", CRUDUpdateParamMethodParameterHandler.class.getName());
        logger.info("CRUDHandler 实现类  -->  {}", this.crudHandlerClass.getName());
    }

    /**
     * 获取 crudHandler
     *
     * @return c
     */
    public Class<? extends CRUDHandler> crudHandlerClass() {
        return crudHandlerClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "SCX_EXT-" + ScxModule.super.name();
    }

}
