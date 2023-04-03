package cool.scx.ext.cms.directive;

import cool.scx.dao.Query;
import cool.scx.dao.query.OrderByType;
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
        var currentPage = ObjectUtils.convertValue(params.get("currentPage"), Long.class);
        var pageSize = ObjectUtils.convertValue(params.get("pageSize"), Long.class);
        if (id != null) {
            query.equal("id", id);
        }

        if (pageSize != null && pageSize >= 0) {
            if (currentPage != null && currentPage >= 0) {
                query.setLimit(currentPage * pageSize, pageSize);
            } else {
                query.setLimit(pageSize);
            }
        }

        if (orderByColumn != null && sortType != null) {
            query.orderBy().add(orderByColumn, OrderByType.of(sortType));
        }
        return query;
    }

}
