package cool.scx.ext.crud.exception;

import cool.scx.http.exception.impl.BadRequestException;
import cool.scx.vo.Json;

/**
 * a
 */
public final class PaginationParametersError extends BadRequestException {

    /**
     * a
     *
     * @param currentPage a
     * @param pageSize    a
     */
    public PaginationParametersError(Integer currentPage, Integer pageSize) {
        super(Json.fail("pagination-parameters-error").put("info", "currentPage 和 pageSize 均不能小于 0").put("currentPage", currentPage).put("pageSize", pageSize).toJson(""));
    }

}