package cool.scx.ext.cms.directive;

import cool.scx.core.annotation.ScxService;
import cool.scx.core.base.BaseTemplateDirective;
import cool.scx.ext.cms.content.ContentService;
import cool.scx.util.ObjectUtils;

import java.util.Map;

/**
 * 自定义标签测试
 *
 * @author scx567888
 * @version 0.3.6
 */
@ScxService
public class ContentListDirective implements BaseTemplateDirective {


    private final ContentService contentService;

    /**
     * a
     *
     * @param contentService a
     */
    public ContentListDirective(ContentService contentService) {
        this.contentService = contentService;
    }

    /**
     * {@inheritDoc}
     * <p>
     * getResults
     */
    @Override
    public Object handle(Map<String, Object> params) {
        var query = ListDirectiveHelper.createNormalListQuery(params);
        var channelID = ObjectUtils.convertValue(params.get("channelID"), Long.class);
        var hasContentTitleImage = ObjectUtils.convertValue(params.get("hasContentTitleImage"), Boolean.class);

        if (channelID != null) {
            query.equal("channelID", channelID);
        }
        if (hasContentTitleImage != null) {
            if (hasContentTitleImage) {
                query.isNotNull("contentTitleImage");
            } else {
                query.isNull("contentTitleImage");
            }
        }

        return contentService.list(query);
    }

    /**
     * {@inheritDoc}
     * <p>
     * getResults
     */
    @Override
    public String directiveName() {
        return "content_list_tag";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String variableName() {
        return "content_list";
    }

}
