package cool.scx.ext.office;

import cool.scx.ScxModule;

/**
 * 办公模块 (暂时都是一些工具类)
 */
public class OfficeModule implements ScxModule {

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "SCX_EXT-" + ScxModule.super.name();
    }

}
