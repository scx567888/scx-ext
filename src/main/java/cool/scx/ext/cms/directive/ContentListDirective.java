package cool.scx.ext.cms.directive;

import cool.scx.annotation.ScxService;
import cool.scx.base.BaseTemplateDirective;
import cool.scx.bo.Query;
import cool.scx.ext.cms.content.ContentService;
import cool.scx.sql.order_by.OrderByType;

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
        var query = new Query();
        Object id = params.get("id");
        Object channelID = params.get("channelID");
        Object hasContentTitleImage = params.get("hasContentTitleImage");
        Object orderByColumn = params.get("orderByColumn");
        Object sortType = params.get("sortType");
        Integer limit = params.get("limit") != null ? Integer.valueOf(params.get("limit").toString()) : null;
        Integer page = params.get("page") != null ? Integer.valueOf(params.get("page").toString()) : null;
        if (id != null) {
            query.equal("id", Long.valueOf(id.toString()));
        }
        if (channelID != null) {
            query.equal("channelID", Long.valueOf(channelID.toString()));
        }
        if (hasContentTitleImage != null) {
            if (Boolean.parseBoolean(hasContentTitleImage.toString())) {
                query.isNotNull("contentTitleImage");
            } else {
                query.isNull("contentTitleImage");
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
            query.orderBy().add(orderByColumn.toString(), OrderByType.of(sortType.toString()));
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
