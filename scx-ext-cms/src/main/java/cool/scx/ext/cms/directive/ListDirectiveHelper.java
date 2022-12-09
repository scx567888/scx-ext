package cool.scx.ext.cms.directive;

import cool.scx.sql.base.Query;
import cool.scx.sql.order_by.OrderByType;
import cool.scx.util.ObjectUtils;

import java.util.Map;

/**
 * a
 *
 * @author scx567888
 * @version 1.7.7
 */
public final class ListDirectiveHelper {

    /**
     * a
     *
     * @param params a
     * @return a
     */
    public static Query createNormalListQuery(Map<?, ?> params) {
        var query = new Query();
        var id = ObjectUtils.convertValue(params.get("id"), Long.class);
        var orderByColumn = ObjectUtils.convertValue(params.get("orderByColumn"), String.class);
        var sortType = ObjectUtils.convertValue(params.get("sortType"), String.class);
        var limit = ObjectUtils.convertValue(params.get("limit"), Integer.class);
        var page = ObjectUtils.convertValue(params.get("page"), Integer.class);
        if (id != null) {
            query.equal("id", id);
        }

        if (limit != null && limit >= 0) {
            if (page != null && page >= 0) {
                query.setPagination(page, limit);
            } else {
                query.setPagination(limit);
            }
        }

        if (orderByColumn != null && sortType != null) {
            query.orderBy().add(orderByColumn, OrderByType.of(sortType));
        }
        return query;
    }

}
