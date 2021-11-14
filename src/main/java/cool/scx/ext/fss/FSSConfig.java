package cool.scx.ext.fss;

import cool.scx.ScxContext;
import cool.scx.config.handler.impl.AppRootHandler;
import cool.scx.util.FileUtils;
import cool.scx.util.ansi.Ansi;

import java.io.File;

/**
 * 核心模块配置文件
 *
 * @author scx567888
 * @version 1.1.2
 */
public class FSSConfig {

    private static FSSEasyToUse fssEasyToUse;

    /**
     * <p>initConfig.</p>
     */
    protected static void initConfig() {
        fssEasyToUse = new FSSEasyToUse();
    }

    /**
     * <p>uploadFilePath.</p>
     *
     * @return a {@link java.io.File} object.
     */
    public static File uploadFilePath() {
        return fssEasyToUse.uploadFilePath;
    }

    private static String formatFileSize(String s) {
        return FileUtils.longToDisplaySize(FileUtils.displaySizeToLong(s));
    }

    static class FSSEasyToUse {

        /**
         * 文件上传路径
         */
        final File uploadFilePath;

        FSSEasyToUse() {
            uploadFilePath = ScxContext.config().get("fss.physical-file-path", new AppRootHandler("AppRoot:/FSS_FILES/", ScxContext.appRoot()));
            Ansi.out().magenta("Y FSS 物理文件存储位置                   \t -->\t " + uploadFilePath).println();
        }
    }

}
