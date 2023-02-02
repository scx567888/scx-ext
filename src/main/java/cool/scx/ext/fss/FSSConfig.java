package cool.scx.ext.fss;

import cool.scx.config.handler_impl.AppRootHandler;
import cool.scx.core.Scx;
import cool.scx.core.ScxContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

/**
 * 核心模块配置文件
 *
 * @author scx567888
 * @version 1.1.2
 */
public class FSSConfig {

    /**
     * Constant <code>logger</code>
     */
    private static final Logger logger = LoggerFactory.getLogger(FSSConfig.class);

    /**
     * 文件上传路径
     */
    private static Path uploadFilePath;

    /**
     * <p>initConfig.</p>
     */
    static void initConfig(Scx scx) {
        uploadFilePath = scx.scxConfig().get("fss.physical-file-path", AppRootHandler.of(ScxContext.environment(), "AppRoot:/FSS_FILES/"));
        logger.debug("FSS 物理文件存储位置  -->  {}", uploadFilePath);
    }

    /**
     * <p>uploadFilePath.</p>
     *
     * @return a {@link java.io.File} object.
     */
    public static Path uploadFilePath() {
        return uploadFilePath;
    }

}
