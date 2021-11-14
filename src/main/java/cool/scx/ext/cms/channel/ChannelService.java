package cool.scx.ext.cms.channel;

import cool.scx.annotation.ScxService;
import cool.scx.base.BaseService;
import cool.scx.bo.Query;

import java.sql.SQLException;

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
     * @throws java.sql.SQLException e
     */
    public Channel getChannelByPath(String path) throws SQLException {
        return get(new Query().equal("channelPath", path));
    }

}
