package cool.scx.ext.cms.directive;

import cool.scx.data.Query;
import cool.scx.data.QueryImpl;
import cool.scx.data.query.*;
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
    public static QueryImpl createNormalListQuery(Map<?, ?> params) {
        var query = Query.query();
        var orderByColumn = ObjectUtils.convertValue(params.get("orderByColumn"), String.class);
        var sortType = ObjectUtils.convertValue(params.get("sortType"), String.class);
        var currentPage = ObjectUtils.convertValue(params.get("currentPage"), Long.class);
        var pageSize = ObjectUtils.convertValue(params.get("pageSize"), Long.class);

        if (pageSize != null && pageSize >= 0) {
            if (currentPage != null && currentPage >= 0) {
                query.offset(currentPage * pageSize).limit(pageSize);
            } else {
                query.limit(pageSize);
            }
        }

        if (orderByColumn != null && sortType != null) {
            query.orderBy(new OrderByBody(orderByColumn, OrderByType.of(sortType)));
        }
        return query;
    }

    public static WhereBodySet createNormalListWhereBodySet(Map<?, ?> params) {
        var query = Query.andSet();
        var id = ObjectUtils.convertValue(params.get("id"), Long.class);
        query.eq("id", id, WhereOption.SKIP_IF_NULL);
        return query;
    }

}
