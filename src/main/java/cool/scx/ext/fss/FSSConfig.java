package cool.scx.ext.fss;

import cool.scx.ScxContext;
import cool.scx.config.handler.impl.AppRootHandler;
import cool.scx.util.ansi.Ansi;

import java.io.File;

/**
 * 核心模块配置文件
 *
 * @author scx567888
 * @version 1.1.2
 */
public class FSSConfig {

    /**
     * 文件上传路径
     */
    private static File uploadFilePath;

    /**
     * <p>initConfig.</p>
     */
    static void initConfig() {
        uploadFilePath = ScxContext.config().get("fss.physical-file-path", new AppRootHandler("AppRoot:/FSS_FILES/", ScxContext.appRoot()));
        Ansi.out().magenta("Y FSS 物理文件存储位置                   \t -->\t " + uploadFilePath).println();
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
