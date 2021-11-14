package cool.scx.ext.fss;

import cool.scx.ScxModule;
import cool.scx.util.ansi.Ansi;

/**
 * 提供基本的文件上传及下载 (展示)的功能
 *
 * @author scx567888
 * @version 1.0.10
 */
public class FSSModule implements ScxModule {

    private final Class<? extends FSSHandler> fssHandlerClass;

    /**
     * <p>Constructor for FSSModule.</p>
     *
     * @param fssHandlerClass a {@link java.lang.Class} object
     */
    public FSSModule(Class<? extends FSSHandler> fssHandlerClass) {
        this.fssHandlerClass = fssHandlerClass;
    }

    /**
     * <p>Constructor for FSSModule.</p>
     */
    public FSSModule() {
        this.fssHandlerClass = FSSHandlerImpl.class;
    }

    /**
     * {@inheritDoc}
     * <p>
     * start
     */
    @Override
    public void start() {
        Ansi.out().brightBlue("FSSHandler 实现类  -->  " + this.fssHandlerClass.getName()).println();
        FSSConfig.initConfig();
    }

    /**
     * <p>fssHandlerClass.</p>
     *
     * @return a {@link java.lang.Class} object
     */
    public Class<? extends FSSHandler> fssHandlerClass() {
        return fssHandlerClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "SCX_EXT-" + ScxModule.super.name();
    }

}
