package cool.scx.ext.cms.channel;

import cool.scx.core.annotation.ScxService;
import cool.scx.core.base.BaseModelService;

import static cool.scx.data.QueryBuilder.eq;
import static cool.scx.data.QueryBuilder.query;

/**
 * ColumnService
 *
 * @author scx567888
 * @version 0.3.6
 */
@ScxService
public class ChannelService extends BaseModelService<Channel> {

    /**
     * <p>getChannelByPath.</p>
     *
     * @param path a
     * @return a
     */
    public Channel getChannelByPath(String path) {
        return get(query().where(eq("channelPath", path)));
    }

}
