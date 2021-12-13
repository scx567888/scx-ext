package cool.scx.ext.fss;

import cool.scx.ScxContext;
import cool.scx.config.handler.impl.AppRootHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * 核心模块配置文件
 *
 * @author scx567888
 * @version 1.1.2
 */
public class FSSConfig {

    private static final Logger logger = LoggerFactory.getLogger(FSSConfig.class);

    /**
     * 文件上传路径
     */
    private static File uploadFilePath;

    /**
     * <p>initConfig.</p>
     */
    static void initConfig() {
        uploadFilePath = ScxContext.config().get("fss.physical-file-path", new AppRootHandler("AppRoot:/FSS_FILES/", ScxContext.appRoot()));
        logger.debug("FSS 物理文件存储位置  -->  {}", uploadFilePath);
    }

    /**
     * <p>uploadFilePath.</p>
     *
     * @return a {@link java.io.File} object.
     */
    public static File uploadFilePath() {
        return uploadFilePath;
    }

}
