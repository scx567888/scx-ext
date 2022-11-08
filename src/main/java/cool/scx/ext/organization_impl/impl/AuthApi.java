package cool.scx.ext.organization_impl.impl;

import cool.scx.core.annotation.FromBody;
import cool.scx.core.annotation.ScxMapping;
import cool.scx.core.enumeration.HttpMethod;
import cool.scx.core.http.exception.UnauthorizedException;
import cool.scx.core.vo.BaseVo;
import cool.scx.core.vo.DataJson;
import cool.scx.core.vo.Json;
import cool.scx.ext.organization.base.BaseAuthApi;
import cool.scx.ext.organization.base.BaseUserService;
import io.vertx.ext.web.RoutingContext;

/**
 * 默认认证 api 推荐使用
 * 也可以不用此 api 但需要将 自定义 AuthHandler 的实现中的方法清空
 *
 * @author scx567888
 * @version 0.3.6
 */
@ScxMapping("/api/auth")
public class AuthApi extends BaseAuthApi<User> {

    protected AuthApi(AuthHandler authHandler, BaseUserService<User> userService) {
        super(authHandler, userService);
    }

    /**
     * <p>checkThatThereIsAtLeastOneAdmin.</p>
     *
     * @param id a {@link java.lang.Long} object
     * @return a {@link cool.scx.core.vo.BaseVo} object
     */
    @ScxMapping(method = HttpMethod.POST)
    public BaseVo checkThatThereIsAtLeastOneAdmin(@FromBody(required = false) Long id) {
        return ((AuthHandler) authHandler).checkThatThereIsAtLeastOneAdmin(id) ? Json.ok() : Json.fail();
    }

    @Override
    public BaseVo info(RoutingContext routingContext) throws UnauthorizedException {
        var user = authHandler.getCurrentUser(routingContext);
        //返回登录用户的信息给前台 含用户基本信息还有的所有角色的权限
        return DataJson.ok().data(new UserInfo(user, authHandler.getPerms(user)));
    }

}
