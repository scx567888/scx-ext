package cool.scx.ext.crud;

import cool.scx.core.base.BaseModel;
import cool.scx.data.Query;
import cool.scx.data.jdbc.ColumnFilter;
import cool.scx.data.jdbc.ColumnMapping;
import cool.scx.data.jdbc.mapping.Table;
import cool.scx.data.query.Logic;
import cool.scx.data.query.OrderByBody;
import cool.scx.data.query.OrderByType;
import cool.scx.data.query.WhereType;
import cool.scx.ext.crud.exception.*;
import cool.scx.mvc.exception.BadRequestException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
    public static ColumnFilter.FilterMode checkFilterMode(String filterMode) throws UnknownWhereTypeException {
        try {
            return ColumnFilter.FilterMode.of(filterMode);
        } catch (Exception ignored) {
            throw new UnknownFilterModeException(filterMode);
        }
    }

    /**
     * 处理分页
     *
     * @param query          a
     * @param crudPagination a
     */
    public static void checkPagination(Query query, CRUDPagination crudPagination) {
        var pageSize = crudPagination.pageSize;
        var currentPage = crudPagination.currentPage;
        if (pageSize != null) {
            if (pageSize >= 0) {
                if (currentPage == null) {
                    query.limit(pageSize);
                } else if (currentPage >= 0) {
                    query.limit(currentPage * pageSize, pageSize);
                } else {
                    throw new PaginationParametersErrorException(currentPage, pageSize);
                }
            } else {
                throw new PaginationParametersErrorException(currentPage, pageSize);
            }
        }
    }

    /**
     * <p>Getter for the field <code>pagination</code>.</p>
     *
     * @param query a {@link cool.scx.data.Query} object
     */
    private void getPagination(Query query) {
        if (this.pagination != null) {
            try {
                checkPagination(query, this.pagination);
            } catch (Exception ignored) {

            }
        }
    }

    /**
     * <p>getPaginationOrThrow.</p>
     *
     * @param query a {@link cool.scx.data.Query} object
     */
    private void getPaginationOrThrow(Query query) {
        if (this.pagination != null) {
            checkPagination(query, this.pagination);
        }
    }

    /**
     * <p>getOrderBy.</p>
     *
     * @param query      a {@link cool.scx.data.Query} object
     * @param modelClass a {@link java.lang.Class} object
     */
    public void getOrderBy(Query query, Class<? extends BaseModel> modelClass) {
        if (this.orderByBodyList != null) {
            var l = new ArrayList<OrderByBody>();
            for (var orderByBody : this.orderByBodyList) {
                if (orderByBody.fieldName != null && orderByBody.sortType != null) {
                    try {
                        //校验 fieldName 是否正确
                        CRUDHelper.checkFieldName(modelClass, orderByBody.fieldName);
                        //检查 sortType 是否正确
                        var sortType = checkSortType(orderByBody.fieldName, orderByBody.sortType);
                        l.add(OrderByBody.of(orderByBody.fieldName, sortType));
                    } catch (Exception ignored) {

                    }
                }
            }
            query.orderBy(l.toArray(OrderByBody[]::new));
        }
    }

    /**
     * <p>getOrderByOrThrow.</p>
     *
     * @param query      a {@link cool.scx.data.Query} object
     * @param modelClass a {@link java.lang.Class} object
     */
    public void getOrderByOrThrow(Query query, Class<? extends BaseModel> modelClass) {
        if (this.orderByBodyList != null) {
            var l = new ArrayList<OrderByBody>();
            for (var orderByBody : this.orderByBodyList) {
                if (orderByBody.fieldName != null && orderByBody.sortType != null) {
                    //校验 fieldName 是否正确
                    CRUDHelper.checkFieldName(modelClass, orderByBody.fieldName);
                    //检查 sortType 是否正确
                    var sortType = checkSortType(orderByBody.fieldName, orderByBody.sortType);
                    l.add(OrderByBody.of(orderByBody.fieldName, sortType));
                }
            }
            query.orderBy(l.toArray(OrderByBody[]::new));
        }
    }

    /**
     * <p>getWhere.</p>
     *
     * @param query      a {@link cool.scx.data.Query} object
     * @param modelClass a {@link java.lang.Class} object
     */
    public void getWhere(Query query, Class<? extends BaseModel> modelClass) {
        if (this.whereBodyList != null) {
            var l = Logic.andSet();
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
            query.where(l);
        }
    }

    /**
     * <p>getWhereOrThrow.</p>
     *
     * @param query      a {@link cool.scx.data.Query} object
     * @param modelClass a {@link java.lang.Class} object
     */
    public void getWhereOrThrow(Query query, Class<? extends BaseModel> modelClass) {
        if (this.whereBodyList != null) {
            var l = Logic.andSet();
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
            query.where(l);
        }
    }

    /**
     * 获取 Query
     *
     * @param modelClass a
     * @return a
     * @throws cool.scx.mvc.exception.BadRequestException if any.
     */
    public Query getQueryOrThrow(Class<? extends BaseModel> modelClass) throws BadRequestException {
        var query = new Query();
        //先处理一下分页
        getPaginationOrThrow(query);
        getOrderByOrThrow(query, modelClass);
        getWhereOrThrow(query, modelClass);
        return query;
    }

    /**
     * <p>getQuery.</p>
     *
     * @param modelClass a {@link java.lang.Class} object
     * @return a {@link cool.scx.data.Query} object
     * @throws cool.scx.mvc.exception.BadRequestException if any.
     */
    public Query getQuery(Class<? extends BaseModel> modelClass) throws BadRequestException {
        var query = new Query();
        //先处理一下分页
        getPagination(query);
        getOrderBy(query, modelClass);
        getWhere(query, modelClass);
        return query;
    }

    /**
     * 获取 b
     *
     * @param modelClass      a
     * @param scxDaoTableInfo a
     * @return a
     */
    public ColumnFilter getSelectFilterOrThrow(Class<? extends BaseModel> modelClass, Table<? extends ColumnMapping> scxDaoTableInfo) {
        if (selectFilterBody == null) {
            return ColumnFilter.ofExcluded();
        }
        var filterMode = checkFilterMode(selectFilterBody.filterMode);
        var legalFieldName = selectFilterBody.fieldNames != null ? Arrays.stream(selectFilterBody.fieldNames).map(fieldName -> CRUDHelper.checkFieldName(modelClass, fieldName)).toArray(String[]::new) : new String[0];
        var selectFilter = switch (filterMode) {
            case EXCLUDED -> ColumnFilter.ofExcluded().addExcluded(legalFieldName);
            case INCLUDED -> ColumnFilter.ofIncluded().addIncluded(legalFieldName);
        };
        //防止空列查询
        if (selectFilter.filter(scxDaoTableInfo).length == 0) {
            throw new EmptySelectColumnException(filterMode, selectFilterBody.fieldNames);
        }
        return selectFilter;
    }

    /**
     * <p>getSelectFilter.</p>
     *
     * @param modelClass      a {@link java.lang.Class} object
     * @param scxDaoTableInfo a {@link Table} object
     * @return a {@link cool.scx.data.jdbc.ColumnFilter} object
     */
    public ColumnFilter getSelectFilter(Class<? extends BaseModel> modelClass, Table<? extends ColumnMapping> scxDaoTableInfo) {
        if (selectFilterBody == null) {
            return ColumnFilter.ofExcluded();
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
            case EXCLUDED -> ColumnFilter.ofExcluded().addExcluded(legalFieldName);
            case INCLUDED -> ColumnFilter.ofIncluded().addIncluded(legalFieldName);
        };
        //防止空列查询
        if (selectFilter.filter(scxDaoTableInfo).length == 0) {
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
