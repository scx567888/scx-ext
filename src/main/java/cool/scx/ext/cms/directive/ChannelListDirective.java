package cool.scx.ext.cms.directive;

import cool.scx.annotation.ScxService;
import cool.scx.base.BaseTemplateDirective;
import cool.scx.bo.Query;
import cool.scx.ext.cms.channel.ChannelService;

import java.sql.SQLException;
import java.util.Map;

/**
 * 自定义标签测试
 *
 * @author scx567888
 * @version 0.3.6
 */
@ScxService
public class ChannelListDirective implements BaseTemplateDirective {

    /**
     * a
     */
    private final ChannelService channelService;

    /**
     * <p>Constructor for ColumnListDirective.</p>
     *
     * @param channelService a {@link cool.scx.ext.cms.channel.ChannelService} object.
     */
    public ChannelListDirective(ChannelService channelService) {
        this.channelService = channelService;
    }

    /**
     * {@inheritDoc}
     * <p>
     * 重写方法
     */
    @Override
    public Object handle(Map<String, Object> params) {
        var query = new Query();
        Object id = params.get("id");
        Object parentID = params.get("parentID");
        Object hasChannelTitleImage = params.get("hasChannelTitleImage");
        Object orderByColumn = params.get("orderByColumn");
        Object sortType = params.get("sortType");
        Integer limit = params.get("limit") != null ? Integer.valueOf(params.get("limit").toString()) : null;
        Integer page = params.get("page") != null ? Integer.valueOf(params.get("page").toString()) : null;
        if (id != null) {
            query.equal("id", Long.valueOf(id.toString()));
        }
        if (parentID != null) {
            query.equal("parentID", Long.valueOf(parentID.toString()));
        }
        if (hasChannelTitleImage != null) {
            if (Boolean.parseBoolean(hasChannelTitleImage.toString())) {
                query.isNotNull("channelTitleImage");
            } else {
                query.isNull("channelTitleImage");
            }
        }

        if (limit != null && limit >= 0) {
            if (page != null && page >= 0) {
                query.setPagination(page, limit);
            } else {
                query.setPagination(limit);
            }
        }

        if (orderByColumn != null && sortType != null) {
            query.addOrderBy(orderByColumn.toString(), sortType.toString());
        }

        try {
            return channelService.list(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * directiveName
     */
    @Override
    public String directiveName() {
        return "channel_list_tag";
    }

    /**
     * {@inheritDoc}
     * <p>
     * directiveName
     */
    @Override
    public String variableName() {
        return "channel_list";
    }

}
