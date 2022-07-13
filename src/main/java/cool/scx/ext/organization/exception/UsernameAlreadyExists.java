package cool.scx.ext.organization.exception;

import cool.scx.core.vo.BaseVo;
import cool.scx.core.vo.Json;

/**
 * <p>UsernameAlreadyExists class.</p>
 *
 * @author scx567888
 * @version 1.11.8
 */
public class UsernameAlreadyExists extends AuthException {

    /**
     * {@inheritDoc}
     */
    @Override
    public BaseVo toBaseVo() {
        return Json.fail("username-already-exists");
    }

}
