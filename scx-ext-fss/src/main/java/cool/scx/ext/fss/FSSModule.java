package cool.scx.ext.fss;

import cool.scx.core.Scx;
import cool.scx.core.ScxModule;

import java.lang.System.Logger;

/**
 * 提供基本的文件上传及下载 (展示)的功能
 *
 * @author scx567888
 * @version 1.0.10
 */
public class FSSModule extends ScxModule {

    private static final Logger logger = System.getLogger(FSSModule.class.getName());

    public FSSModule() {

    }

    @Override
    public void start(Scx scx) {
        FSSConfig.initConfig(scx);
    }

    @Override
    public String name() {
        return "SCX_EXT-" + super.name();
    }

}
