package cool.scx.ext.crud;

import cool.scx.core.base.BaseModel;
import cool.scx.data.FieldFilter;
import cool.scx.data.Query;
import cool.scx.data.QueryImpl;
import cool.scx.data.field_filter.FilterMode;
import cool.scx.data.jdbc.ColumnMapping;
import cool.scx.data.jdbc.FieldFilterHelper;
import cool.scx.data.jdbc.mapping.Table;
import cool.scx.data.query.*;
import cool.scx.ext.crud.exception.*;
import cool.scx.mvc.exception.BadRequestException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static cool.scx.data.FieldFilter.ofExcluded;
import static cool.scx.data.FieldFilter.ofIncluded;
import static cool.scx.data.Query.query;

/**
 * a
 *
 * @author scx567888
 * @version 1.10.8
 */
public final class CRUDListParam {

    /**
     * 分页参数
     */
    public CRUDPagination pagination;

    /**
     * 排序参数
     */
    public List<CRUDOrderByBody> orderByBodyList;

    /**
     * 查询参数
     */
    public List<CRUDWhereBody> whereBodyList;

    /**
     * 查询列过滤参数
     */
    public CRUDSelectFilterBody selectFilterBody;

    /**
     * 检查 where 类型
     *
     * @param fieldName    f
     * @param strWhereType s
     * @return s
     * @throws cool.scx.ext.crud.exception.UnknownWhereTypeException s
     */
    public static WhereType checkWhereType(String fieldName, String strWhereType) throws UnknownWhereTypeException {
        try {
            return WhereType.of(strWhereType);
        } catch (Exception ignored) {
            throw new UnknownWhereTypeException(fieldName, strWhereType);
        }
    }

    /**
     * a
     *
     * @param fieldName   a
     * @param strSortType a
     * @return a
     * @throws cool.scx.ext.crud.exception.UnknownSortTypeException a
     */
    public static OrderByType checkSortType(String fieldName, String strSortType) throws UnknownSortTypeException {
        try {
            return OrderByType.of(strSortType);
        } catch (Exception ignored) {
            throw new UnknownSortTypeException(fieldName, strSortType);
        }
    }

    /**
     * 检查 whereBody 参数数量是否合法
     *
     * @param fieldName f
     * @param whereType w
     * @param value1    v
     * @param value2    v
     * @throws cool.scx.ext.crud.exception.WhereBodyParametersSizeErrorException v
     */
    public static void checkWhereBodyParametersSize(String fieldName, WhereType whereType, Object value1, Object value2) throws WhereBodyParametersSizeErrorException {
        AtomicInteger paramSize = new AtomicInteger();
        if (value1 != null) {
            paramSize.set(paramSize.get() + 1);
        }
        if (value2 != null) {
            paramSize.set(paramSize.get() + 1);
        }

        if (whereType.paramSize() != paramSize.get()) {
            throw new WhereBodyParametersSizeErrorException(fieldName, whereType, paramSize.get());
        }
    }

    /**
     * 检查 filterMode 是否正确
     *
     * @param filterMode f
     * @return a
     * @throws cool.scx.ext.crud.exception.UnknownWhereTypeException a
     */
    public static FilterMode checkFilterMode(String filterMode) throws UnknownWhereTypeException {
        try {
            return FilterMode.of(filterMode);
        } catch (Exception ignored) {
            throw new UnknownFilterModeException(filterMode);
        }
    }

    /**
     * 处理分页
     *
     * @param crudPagination a
     * @return a
     */
    public static LimitInfo checkPagination(CRUDPagination crudPagination) {
        var pageSize = crudPagination.pageSize;
        var currentPage = crudPagination.currentPage;
        if (pageSize == null) {
            return new LimitInfo();
        }
        if (pageSize < 0) {
            throw new PaginationParametersErrorException(currentPage, pageSize);
        }
        if (currentPage == null) {
            return new LimitInfo().limit(pageSize);
        }
        if (currentPage < 0) {
            throw new PaginationParametersErrorException(currentPage, pageSize);
        }
        return new LimitInfo().offset(currentPage * pageSize).limit(pageSize);
    }

    /**
     * <p>Getter for the field <code>pagination</code>.</p>
     *
     * @return a
     */
    public LimitInfo getLimitInfo() {
        if (this.pagination != null) {
            try {
                return checkPagination(this.pagination);
            } catch (Exception ignored) {

            }
        }
        return new LimitInfo();
    }

    /**
     * <p>getPaginationOrThrow.</p>
     *
     * @return a
     */
    public LimitInfo getLimitInfoOrThrow() {
        if (this.pagination != null) {
            return checkPagination(this.pagination);
        }
        return new LimitInfo();
    }

    /**
     * <p>getOrderBy.</p>
     *
     * @param modelClass a {@link java.lang.Class} object
     */
    public OrderByBody[] getOrderByClauses(Class<? extends BaseModel> modelClass) {
        var l = new ArrayList<OrderByBody>();
        if (this.orderByBodyList != null) {
            for (var orderByBody : this.orderByBodyList) {
                if (orderByBody.fieldName != null && orderByBody.sortType != null) {
                    try {
                        //校验 fieldName 是否正确
                        CRUDHelper.checkFieldName(modelClass, orderByBody.fieldName);
                        //检查 sortType 是否正确
                        var sortType = checkSortType(orderByBody.fieldName, orderByBody.sortType);
                        l.add(new OrderByBody(orderByBody.fieldName, sortType));
                    } catch (Exception ignored) {

                    }
                }
            }
        }
        return l.toArray(OrderByBody[]::new);
    }

    /**
     * <p>getOrderByOrThrow.</p>
     *
     * @param modelClass a {@link java.lang.Class} object
     * @return a
     */
    public OrderByBody[] getOrderByClausesOrThrow(Class<? extends BaseModel> modelClass) {
        var l = new ArrayList<OrderByBody>();
        if (this.orderByBodyList != null) {
            for (var orderByBody : this.orderByBodyList) {
                if (orderByBody.fieldName != null && orderByBody.sortType != null) {
                    //校验 fieldName 是否正确
                    CRUDHelper.checkFieldName(modelClass, orderByBody.fieldName);
                    //检查 sortType 是否正确
                    var sortType = checkSortType(orderByBody.fieldName, orderByBody.sortType);
                    l.add(new OrderByBody(orderByBody.fieldName, sortType));
                }
            }
        }
        return l.toArray(OrderByBody[]::new);
    }

    /**
     * <p>getWhere.</p>
     *
     * @param modelClass a {@link java.lang.Class} object
     */
    public WhereBodySet getWhereBodySet(Class<? extends BaseModel> modelClass) {
        var l = Query.andSet();
        if (this.whereBodyList != null) {
            for (var crudWhereBody : this.whereBodyList) {
                if (crudWhereBody.fieldName != null && crudWhereBody.whereType != null) {
                    try {
                        //校验 fieldName 是否正确
                        CRUDHelper.checkFieldName(modelClass, crudWhereBody.fieldName);
                        //检查 whereType 是否正确
                        var whereType = checkWhereType(crudWhereBody.fieldName, crudWhereBody.whereType);
                        //检查参数数量是否正确
                        checkWhereBodyParametersSize(crudWhereBody.fieldName, whereType, crudWhereBody.value1, crudWhereBody.value2);
                        if (whereType.paramSize() == 0) {
                            l.add0(crudWhereBody.fieldName, whereType);
                        } else if (whereType.paramSize() == 1) {
                            l.add1(crudWhereBody.fieldName, whereType, crudWhereBody.value1);
                        } else if (whereType.paramSize() == 2) {
                            l.add2(crudWhereBody.fieldName, whereType, crudWhereBody.value1, crudWhereBody.value2);
                        }
                    } catch (Exception ignored) {

                    }
                }
            }
        }
        return l;
    }

    /**
     * <p>getWhereOrThrow.</p>
     *
     * @param modelClass a {@link java.lang.Class} object
     * @return a
     */
    public WhereBodySet getWhereBodySetOrThrow(Class<? extends BaseModel> modelClass) {
        var l = Query.andSet();
        if (this.whereBodyList != null) {
            for (var crudWhereBody : this.whereBodyList) {
                if (crudWhereBody.fieldName != null && crudWhereBody.whereType != null) {
                    //校验 fieldName 是否正确
                    CRUDHelper.checkFieldName(modelClass, crudWhereBody.fieldName);
                    //检查 whereType 是否正确
                    var whereType = checkWhereType(crudWhereBody.fieldName, crudWhereBody.whereType);
                    //检查参数数量是否正确
                    checkWhereBodyParametersSize(crudWhereBody.fieldName, whereType, crudWhereBody.value1, crudWhereBody.value2);
                    if (whereType.paramSize() == 0) {
                        l.add0(crudWhereBody.fieldName, whereType);
                    } else if (whereType.paramSize() == 1) {
                        l.add1(crudWhereBody.fieldName, whereType, crudWhereBody.value1);
                    } else if (whereType.paramSize() == 2) {
                        l.add2(crudWhereBody.fieldName, whereType, crudWhereBody.value1, crudWhereBody.value2);
                    }
                }
            }
        }
        return l;
    }

    /**
     * 获取 Query
     *
     * @param modelClass a
     * @return a
     * @throws cool.scx.mvc.exception.BadRequestException if any.
     */
    public QueryImpl getQueryOrThrow(Class<? extends BaseModel> modelClass) throws BadRequestException {
        var whereBodySet = getWhereBodySetOrThrow(modelClass);
        var orderByClauses = getOrderByClausesOrThrow(modelClass);
        var limit = getLimitInfoOrThrow();
        var query = query()
                .where(whereBodySet)
                .groupBy()
                .orderBy((Object[]) orderByClauses);
        if (limit.getOffset() != null) {
            query.offset(limit.getOffset());
        }
        if (limit.getLimit() != null) {
            query.limit(limit.getLimit());
        }
        return query;
    }

    /**
     * <p>getQuery.</p>
     *
     * @param modelClass a {@link java.lang.Class} object
     * @return a {@link cool.scx.data.Query} object
     * @throws cool.scx.mvc.exception.BadRequestException if any.
     */
    public QueryImpl getQuery(Class<? extends BaseModel> modelClass) throws BadRequestException {
        var whereBodySet = getWhereBodySet(modelClass);
        var orderByClauses = getOrderByClauses(modelClass);
        var limit = getLimitInfo();
        var query = query()
                .where(whereBodySet)
                .groupBy()
                .orderBy((Object[]) orderByClauses);
        if (limit.getOffset() != null) {
            query.offset(limit.getOffset());
        }
        if (limit.getLimit() != null) {
            query.limit(limit.getLimit());
        }
        return query;
    }

    /**
     * 获取 b
     *
     * @param modelClass      a
     * @param scxDaoTableInfo a
     * @return a
     */
    public FieldFilter getSelectFilterOrThrow(Class<? extends BaseModel> modelClass, Table<? extends ColumnMapping> scxDaoTableInfo) {
        if (selectFilterBody == null) {
            return ofExcluded();
        }
        var filterMode = checkFilterMode(selectFilterBody.filterMode);
        var legalFieldName = selectFilterBody.fieldNames != null ? Arrays.stream(selectFilterBody.fieldNames).map(fieldName -> CRUDHelper.checkFieldName(modelClass, fieldName)).toArray(String[]::new) : new String[0];
        var selectFilter = switch (filterMode) {
            case EXCLUDED -> ofExcluded(legalFieldName);
            case INCLUDED -> ofIncluded(legalFieldName);
        };
        //防止空列查询
        if (FieldFilterHelper.filter(selectFilter, scxDaoTableInfo).length == 0) {
            throw new EmptySelectColumnException(filterMode, selectFilterBody.fieldNames);
        }
        return selectFilter;
    }

    /**
     * <p>getSelectFilter.</p>
     *
     * @param modelClass      a {@link java.lang.Class} object
     * @param scxDaoTableInfo a {@link Table} object
     * @return a {@link FieldFilter} object
     */
    public FieldFilter getSelectFilter(Class<? extends BaseModel> modelClass, Table<? extends ColumnMapping> scxDaoTableInfo) {
        if (selectFilterBody == null) {
            return ofExcluded();
        }
        var filterMode = checkFilterMode(selectFilterBody.filterMode);
        String[] legalFieldName;
        if (selectFilterBody.fieldNames != null) {
            var list = new ArrayList<String>();
            for (var fieldName : selectFilterBody.fieldNames) {
                try {
                    list.add(CRUDHelper.checkFieldName(modelClass, fieldName));
                } catch (Exception ignored) {

                }
            }
            legalFieldName = list.toArray(new String[0]);
        } else {
            legalFieldName = new String[0];
        }
        var selectFilter = switch (filterMode) {
            case EXCLUDED -> ofExcluded(legalFieldName);
            case INCLUDED -> ofIncluded(legalFieldName);
        };
        //防止空列查询
        if (FieldFilterHelper.filter(selectFilter, scxDaoTableInfo).length == 0) {
            throw new EmptySelectColumnException(filterMode, selectFilterBody.fieldNames);
        }
        return selectFilter;
    }


    /**
     * a
     */
    public static final class CRUDPagination {

        /**
         * 页码
         */
        public Long currentPage;

        /**
         * 每页数据条数
         */
        public Long pageSize;
    }

    /**
     * a
     */
    public static final class CRUDSelectFilterBody {

        /**
         * a
         */
        public String filterMode;

        /**
         * a
         */
        public String[] fieldNames;

    }


    /**
     * a
     *
     * @author scx567888
     * @version 1.7.7
     */
    public static final class CRUDWhereBody {

        /**
         * 字段名称 (注意不是数据库名称)
         */
        public String fieldName;

        /**
         * 类型
         */
        public String whereType;

        /**
         * 因为参数不固定 所以这里用两个参数
         * 参数1
         */
        public Object value1;

        /**
         * 参数2
         */
        public Object value2;

        /**
         * 便于序列化
         */
        public CRUDWhereBody() {

        }

        /**
         * 便于开发人员使用
         *
         * @param fieldName f
         * @param whereType w
         * @param value1    v
         * @param value2    v
         */
        public CRUDWhereBody(String fieldName, String whereType, Object value1, Object value2) {
            this.fieldName = fieldName;
            this.whereType = whereType;
            this.value1 = value1;
            this.value2 = value2;
        }

    }

    /**
     * a
     *
     * @author scx567888
     * @version 1.7.7
     */
    public static final class CRUDOrderByBody {

        /**
         * 字段名称 (注意不是数据库名称)
         */
        public String fieldName;

        /**
         * 排序类型 ASC 和 DESC
         */
        public String sortType;

    }

}
