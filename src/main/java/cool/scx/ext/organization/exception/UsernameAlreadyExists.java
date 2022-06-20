package cool.scx.ext.organization.exception;

import cool.scx.vo.BaseVo;
import cool.scx.vo.Json;

public class UsernameAlreadyExists extends AuthException {

    @Override
    public BaseVo toBaseVo() {
        return Json.fail("username-already-exists");
    }

}
