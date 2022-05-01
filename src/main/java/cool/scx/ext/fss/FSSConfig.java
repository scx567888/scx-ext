package cool.scx.ext.fss;

import cool.scx.ScxContext;
import cool.scx.config.handler.AppRootHandler;
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
    static void initConfig() {
        uploadFilePath = ScxContext.config().get("fss.physical-file-path", new AppRootHandler("AppRoot:/FSS_FILES/", ScxContext.environment())).toPath();
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
