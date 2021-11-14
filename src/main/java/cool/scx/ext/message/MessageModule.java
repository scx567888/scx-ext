package cool.scx.ext.message;

import cool.scx.ScxModule;

/**
 * 提供一些 消息类的功能
 *
 * @author scx567888
 * @version 1.3.0
 */
public class MessageModule implements ScxModule {

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "SCX_EXT-" + ScxModule.super.name();
    }

}
