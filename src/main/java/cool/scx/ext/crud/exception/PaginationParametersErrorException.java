package cool.scx.ext.crud.exception;

import cool.scx.core.http.exception.impl.BadRequestException;
import cool.scx.core.vo.Json;

/**
 * a
 *
 * @author scx567888
 * @version 1.10.8
 */
public final class PaginationParametersErrorException extends BadRequestException {

    /**
     * a
     *
     * @param currentPage a
     * @param pageSize    a
     */
    public PaginationParametersErrorException(Integer currentPage, Integer pageSize) {
        super(Json.fail("pagination-parameters-error").put("info", "currentPage 和 pageSize 均不能小于 0").put("currentPage", currentPage).put("pageSize", pageSize).toJson(""));
    }

}
