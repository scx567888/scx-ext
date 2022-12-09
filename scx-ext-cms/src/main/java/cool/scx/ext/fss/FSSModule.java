package cool.scx.ext.fss;

import cool.scx.core.ScxModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 提供基本的文件上传及下载 (展示)的功能
 *
 * @author scx567888
 * @version 1.0.10
 */
public class FSSModule extends ScxModule {

    /**
     * Constant <code>logger</code>
     */
    private static final Logger logger = LoggerFactory.getLogger(FSSModule.class);

    /**
     * <p>Constructor for FSSModule.</p>
     */
    public FSSModule() {

    }

    /**
     * {@inheritDoc}
     * <p>
     * start
     */
    @Override
    public void start() {
        FSSConfig.initConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "SCX_EXT-" + super.name();
    }

}
