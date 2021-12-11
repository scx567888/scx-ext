package cool.scx.ext.crud;

import cool.scx.ScxModule;
import org.slf4j.LoggerFactory;

/**
 * 提供一些简单的增删改查功能
 *
 * @author scx567888
 * @version 1.3.0
 */
public class CRUDModule implements ScxModule {

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
        var logger = LoggerFactory.getLogger(CRUDModule.class);
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