package cool.scx.ext.fixtable;

import cool.scx.ScxContext;
import cool.scx.ScxModule;
import cool.scx.dao.ScxDaoHelper;

/**
 * <p>FixTableModule class.</p>
 *
 * @author scx567888
 * @version 1.3.0
 */
public class FixTableModule implements ScxModule {

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        ScxContext.execute(() -> ScxDaoHelper.fixAllTable(false));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "SCX_EXT-" + ScxModule.super.name();
    }

}
