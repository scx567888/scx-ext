package cool.scx.ext.auth;

import cool.scx.core.http.exception.NoPermException;
import cool.scx.core.http.exception.UnauthorizedException;
import cool.scx.core.mvc.ScxMappingHandler;
import cool.scx.core.mvc.ScxMappingInterceptor;
import cool.scx.ext.auth.annotation.ApiPerms;
import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static cool.scx.util.StringUtils.notBlank;

/**
 * 拦截器 用于校验权限
 *
 * @author scx567888
 * @version 1.11.7
 */
public final class ApiPermsInterceptor implements ScxMappingInterceptor {

    private final BaseAuthHandler<BaseUser> authHandler;

    /**
     * 缓存池
     */
    private final Map<ScxMappingHandler, AuthPerms> SCX_AUTH_PERMS_CACHE = new HashMap<>();

    /**
     * <p>Constructor for PermsAnnotationInterceptor.</p>
     *
     * @param authHandler a {@link cool.scx.ext.auth.BaseAuthHandler} object
     */
    public ApiPermsInterceptor(BaseAuthHandler<BaseUser> authHandler) {
        this.authHandler = authHandler;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void preHandle(RoutingContext context, ScxMappingHandler scxMappingHandler) {
        var p = getScxAuthPerms(scxMappingHandler);
        if (p.needCheckPerms) {
            //先获取登录的用户
            var currentUser = authHandler.getCurrentUser(context);
            //如果用户为空 则执行未登录处理器
            if (currentUser == null) {
                throw new UnauthorizedException();
            } else if (p.checkPerms
                    && !currentUser.isAdmin
                    && !authHandler.getPerms(currentUser).apiPerms().contains(p.permStr)
            ) {
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
    private AuthPerms getScxAuthPerms(ScxMappingHandler s) {
        var p = SCX_AUTH_PERMS_CACHE.get(s);
        if (p == null) {
            p = new AuthPerms(s.clazz, s.method);
            SCX_AUTH_PERMS_CACHE.put(s, p);
        }
        return p;
    }

    /**
     * 用于缓存 提高性能
     */
    private static final class AuthPerms {

        /**
         * 当前 的权限字符串 规则是  {类名}:{方法名}
         */
        public final String permStr;

        /**
         * 是否启用检查 (这里只针对标记注解的 api 进行检查)
         */
        public final boolean needCheckPerms;

        /**
         * 是否检查权限
         */
        public final boolean checkPerms;

        /**
         * <p>Constructor for ScxAuthPerms.</p>
         *
         * @param clazz  c
         * @param method m
         */
        public AuthPerms(Class<?> clazz, Method method) {
            var defaultPermStr = clazz.getSimpleName() + ":" + method.getName();
            var scxPerms = method.getAnnotation(ApiPerms.class);
            if (scxPerms != null) {
                this.permStr = notBlank(scxPerms.value()) ? scxPerms.value() : defaultPermStr;
                this.checkPerms = scxPerms.checkPerms();
                this.needCheckPerms = true;
            } else {
                this.permStr = defaultPermStr;
                this.checkPerms = false;
                this.needCheckPerms = false;
            }
        }

    }

}
