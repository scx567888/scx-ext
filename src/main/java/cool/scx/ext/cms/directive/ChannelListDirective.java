package cool.scx.ext.cms.directive;

import cool.scx.annotation.ScxService;
import cool.scx.base.BaseTemplateDirective;
import cool.scx.ext.cms.channel.ChannelService;

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
        var query = ListDirectiveHelper.createNormalListQuery(params);
        Object parentID = params.get("parentID");
        Object hasChannelTitleImage = params.get("hasChannelTitleImage");

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

        return channelService.list(query);
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
