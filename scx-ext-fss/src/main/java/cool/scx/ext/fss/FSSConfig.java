package cool.scx.ext.fss;

import cool.scx.config.handler.AppRootHandler;
import cool.scx.core.Scx;

import java.lang.System.Logger;
import java.nio.file.Path;

import static java.lang.System.Logger.Level.DEBUG;

/**
 * 核心模块配置文件
 *
 * @author scx567888
 * @version 1.1.2
 */
public class FSSConfig {

    private static final Logger logger = System.getLogger(FSSConfig.class.getName());

    private static Path uploadFilePath;

    static void initConfig(Scx scx) {
        uploadFilePath = scx.scxConfig().get("fss.physical-file-path", AppRootHandler.of(scx.scxEnvironment(), "AppRoot:/FSS_FILES/"));
        logger.log(DEBUG, "FSS 物理文件存储位置  -->  {0}", uploadFilePath);
    }

    public static Path uploadFilePath() {
        return uploadFilePath;
    }

}
