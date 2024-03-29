package cool.scx.ext.crud.exception;

import cool.scx.mvc.exception.BadRequestException;
import cool.scx.mvc.vo.Result;

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
    public PaginationParametersErrorException(Long currentPage, Long pageSize) {
        super(Result.fail("pagination-parameters-error").put("info", "currentPage 和 pageSize 均不能小于 0").put("currentPage", currentPage).put("pageSize", pageSize).toJson(""));
    }

}
