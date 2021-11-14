package cool.scx.ext.organization.auth;

import cool.scx.exception.NoPermException;
import cool.scx.exception.UnauthorizedException;
import cool.scx.mvc.ScxMappingHandler;
import cool.scx.mvc.interceptor.ScxMappingInterceptor;
import io.vertx.ext.web.RoutingContext;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>AuthScxMappingPreProcessor class.</p>
 *
 * @author scx567888
 * @version 1.3.14
 */
public final class OrganizationAuthInterceptor implements ScxMappingInterceptor {

    /**
     * 缓存池
     */
    private final Map<ScxMappingHandler, OrganizationAuthPerms> SCX_AUTH_PERMS_CACHE = new HashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void preHandle(RoutingContext context, ScxMappingHandler scxMappingHandler) throws Exception {
        var p = getScxAuthPerms(scxMappingHandler);
        if (p.checkedLogin) {
            //先获取登录的用户
            var currentUser = OrganizationAuth.getLoginUser(context);
            //如果用户为空 则执行未登录处理器
            if (currentUser == null) {
                throw new UnauthorizedException();
            } else if (p.checkedPerms && !currentUser.isAdmin && !OrganizationAuth.getPerms(currentUser).contains(p.permStr)) {
                //否则先查看是否需要校验权限 然后查看是否不为 admin 再查看是否权限串中不包含当前权限 都满足则表示需要执行没权限的 handler
                throw new NoPermException();
            }
        }
    }

    /**
     * 根据 ScxMappingHandler 获取  ScxAuthPerms (内部使用了简单的缓存)
     *
     * @param s s
     * @return s
     */
    private OrganizationAuthPerms getScxAuthPerms(ScxMappingHandler s) {
        var p = SCX_AUTH_PERMS_CACHE.get(s);
        if (p == null) {
            var scxMappingHandlerPerms = new OrganizationAuthPerms(s.clazz, s.method);
            SCX_AUTH_PERMS_CACHE.put(s, scxMappingHandlerPerms);
            return scxMappingHandlerPerms;
        }
        return p;
    }

}
