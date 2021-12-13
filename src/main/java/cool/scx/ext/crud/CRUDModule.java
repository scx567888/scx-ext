package cool.scx.ext.crud;

import cool.scx.ScxModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 为 BaseModel 的实现类 提供一套简单的 "单表" 的增删改查 api
 *
 * @author scx567888
 * @version 1.3.0
 */
public class CRUDModule implements ScxModule {

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
     * @param c c
     */
    public CRUDModule(Class<? extends CRUDHandler> c) {
        crudHandlerClass = c;
    }

    @Override
    public void start() {
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