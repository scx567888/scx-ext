package cool.scx.ext.crud;

import cool.scx.annotation.NoColumn;
import cool.scx.base.AbstractFilter;
import cool.scx.base.BaseModel;
import cool.scx.base.Query;
import cool.scx.base.SelectFilter;
import cool.scx.dao.ScxDaoTableInfo;
import cool.scx.ext.crud.exception.*;
import cool.scx.http.exception.impl.BadRequestException;
import cool.scx.sql.order_by.OrderByType;
import cool.scx.sql.where.WhereType;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
     * 检查 fieldName 是否合法
     *
     * @param modelClass m
     * @param fieldName  f
     * @throws cool.scx.ext.crud.exception.UnknownFieldName c
     */
    public static void checkFieldName(Class<?> modelClass, String fieldName) throws UnknownFieldName {
        try {
            var field = modelClass.getField(fieldName);
            if (field.isAnnotationPresent(NoColumn.class)) {
                throw new UnknownFieldName(fieldName);
            }
        } catch (Exception e) {
            throw new UnknownFieldName(fieldName);
        }
    }

    /**
     * 检查 where 类型
     *
     * @param fieldName    f
     * @param strWhereType s
     * @return s
     * @throws cool.scx.ext.crud.exception.UnknownWhereType s
     */
    public static WhereType checkWhereType(String fieldName, String strWhereType) throws UnknownWhereType {
        try {
            return WhereType.of(strWhereType);
        } catch (Exception ignored) {
            throw new UnknownWhereType(fieldName, strWhereType);
        }
    }

    /**
     * a
     *
     * @param fieldName   a
     * @param strSortType a
     * @return a
     * @throws cool.scx.ext.crud.exception.UnknownSortType a
     */
    public static OrderByType checkSortType(String fieldName, String strSortType) throws UnknownSortType {
        try {
            return OrderByType.of(strSortType);
        } catch (Exception ignored) {
            throw new UnknownSortType(fieldName, strSortType);
        }
    }

    /**
     * 检查 whereBody 参数数量是否合法
     *
     * @param fieldName f
     * @param whereType w
     * @param value1    v
     * @param value2    v
     * @throws cool.scx.ext.crud.exception.WhereBodyParametersSizeError v
     */
    public static void checkWhereBodyParametersSize(String fieldName, WhereType whereType, Object value1, Object value2) throws WhereBodyParametersSizeError {
        AtomicInteger paramSize = new AtomicInteger();
        if (value1 != null) {
            paramSize.set(paramSize.get() + 1);
        }
        if (value2 != null) {
            paramSize.set(paramSize.get() + 1);
        }

        if (whereType.paramSize() != paramSize.get()) {
            throw new WhereBodyParametersSizeError(fieldName, whereType, paramSize.get());
        }
    }

    /**
     * 检查 filterMode 是否正确
     *
     * @param filterMode f
     * @return a
     * @throws UnknownWhereType a
     */
    public static AbstractFilter.FilterMode checkFilterMode(String filterMode) throws UnknownWhereType {
        try {
            return AbstractFilter.FilterMode.of(filterMode);
        } catch (Exception ignored) {
            throw new UnknownFilterMode(filterMode);
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
                    query.setPagination(pageSize);
                } else if (currentPage >= 0) {
                    query.setPagination(currentPage, pageSize);
                } else {
                    throw new PaginationParametersError(currentPage, pageSize);
                }
            } else {
                throw new PaginationParametersError(currentPage, pageSize);
            }
        }
    }

    /**
     * 获取 Query
     *
     * @param modelClass a
     * @return a
     * @throws cool.scx.http.exception.impl.BadRequestException a
     */
    public Query getQuery(Class<? extends BaseModel> modelClass) throws BadRequestException {
        var query = new Query();
        //先处理一下分页
        if (this.pagination != null) {
            checkPagination(query, this.pagination);
        }
        if (this.orderByBodyList != null) {
            for (var orderByBody : this.orderByBodyList) {
                if (orderByBody.fieldName != null && orderByBody.sortType != null) {
                    //校验 fieldName 是否正确
                    checkFieldName(modelClass, orderByBody.fieldName);
                    //检查 sortType 是否正确
                    var sortType = checkSortType(orderByBody.fieldName, orderByBody.sortType);
                    query.orderBy().add(orderByBody.fieldName, sortType);
                }
            }
        }
        if (this.whereBodyList != null) {
            for (var crudWhereBody : this.whereBodyList) {
                if (crudWhereBody.fieldName != null && crudWhereBody.whereType != null) {
                    //校验 fieldName 是否正确
                    checkFieldName(modelClass, crudWhereBody.fieldName);
                    //检查 whereType 是否正确
                    var whereType = checkWhereType(crudWhereBody.fieldName, crudWhereBody.whereType);
                    //检查参数数量是否正确
                    checkWhereBodyParametersSize(crudWhereBody.fieldName, whereType, crudWhereBody.value1, crudWhereBody.value2);
                    if (whereType.paramSize() == 0) {
                        query.where().add0(crudWhereBody.fieldName, whereType);
                    } else if (whereType.paramSize() == 1) {
                        query.where().add1(crudWhereBody.fieldName, whereType, crudWhereBody.value1);
                    } else if (whereType.paramSize() == 2) {
                        query.where().add2(crudWhereBody.fieldName, whereType, crudWhereBody.value1, crudWhereBody.value2);
                    }
                }
            }
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
    public SelectFilter getSelectFilter(Class<? extends BaseModel> modelClass, ScxDaoTableInfo scxDaoTableInfo) {
        if (selectFilterBody == null) {
            return SelectFilter.ofExcluded();
        }
        var filterMode = checkFilterMode(selectFilterBody.filterMode);
        var selectFilter = switch (filterMode) {
            case EXCLUDED -> SelectFilter.ofExcluded();
            case INCLUDED -> SelectFilter.ofIncluded();
        };
        if (selectFilterBody.fieldNames != null) {
            for (var fieldName : selectFilterBody.fieldNames) {
                checkFieldName(modelClass, fieldName);
                selectFilter.add(fieldName);
            }
        }
        //防止空列查询
        if (selectFilter.filter(scxDaoTableInfo.columnInfos()).length == 0) {
            throw new EmptySelectColumn(filterMode, selectFilterBody.fieldNames);
        }
        return selectFilter;
    }

    public static final class CRUDPagination {

        /**
         * 页码
         */
        public Integer currentPage;

        /**
         * 每页数据条数
         */
        public Integer pageSize;
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