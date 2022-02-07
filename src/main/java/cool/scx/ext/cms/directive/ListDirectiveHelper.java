package cool.scx.ext.cms.directive;

import cool.scx.bo.Query;
import cool.scx.sql.order_by.OrderByType;

import java.util.Map;

public final class ListDirectiveHelper {

    public static Query createNormalListQuery(Map<?, ?> params) {
        var query = new Query();
        Object id = params.get("id");
        Object orderByColumn = params.get("orderByColumn");
        Object sortType = params.get("sortType");
        Integer limit = params.get("limit") != null ? Integer.valueOf(params.get("limit").toString()) : null;
        Integer page = params.get("page") != null ? Integer.valueOf(params.get("page").toString()) : null;
        if (id != null) {
            query.equal("id", Long.valueOf(id.toString()));
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
        return query;
    }
}
