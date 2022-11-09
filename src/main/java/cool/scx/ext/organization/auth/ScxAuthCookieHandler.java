package cool.scx.ext.organization.auth;

import cool.scx.util.RandomUtils;
import io.vertx.core.Handler;
import io.vertx.core.http.impl.CookieImpl;
import io.vertx.ext.web.RoutingContext;

import static cool.scx.ext.organization.auth.BaseAuthHandler.SCX_AUTH_TOKEN_KEY;

/**
 * <p>ScxAuthCookieHandler class.</p>
 *
 * @author scx567888
 * @version 1.11.8
 */
public final class ScxAuthCookieHandler implements Handler<RoutingContext> {

    /**
     * Constant <code>COOKIE_MAX_AGE=1800000L</code>
     */
    private static final long COOKIE_MAX_AGE = 1800000L;

    /**
     * {@inheritDoc}
     */
    @Override
    public void handle(RoutingContext ctx) {
        if (ctx.request().getCookie(SCX_AUTH_TOKEN_KEY) == null) {
            ctx.request().response().addCookie(new CookieImpl(SCX_AUTH_TOKEN_KEY, RandomUtils.randomUUID()).setMaxAge(COOKIE_MAX_AGE));
        }
        ctx.next();
    }

}
