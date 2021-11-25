package cool.scx.ext.cms.channel;

import cool.scx.annotation.ScxService;
import cool.scx.base.BaseService;
import cool.scx.bo.Query;

/**
 * ColumnService
 *
 * @author scx567888
 * @version 0.3.6
 */
@ScxService
public class ChannelService extends BaseService<Channel> {

    /**
     * @param path a
     * @return a
     */
    public Channel getChannelByPath(String path) {
        return get(new Query().equal("channelPath", path));
    }

}
