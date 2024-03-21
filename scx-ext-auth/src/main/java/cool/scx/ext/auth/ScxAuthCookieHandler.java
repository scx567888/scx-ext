package cool.scx.ext.auth;

import cool.scx.common.util.RandomUtils;
import io.vertx.core.Handler;
import io.vertx.core.http.Cookie;
import io.vertx.core.http.CookieSameSite;
import io.vertx.core.http.impl.CookieImpl;
import io.vertx.ext.web.RoutingContext;

import static cool.scx.ext.auth.BaseAuthHandler.SCX_AUTH_TOKEN_KEY;

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
            Cookie cookie = new CookieImpl(SCX_AUTH_TOKEN_KEY, RandomUtils.randomUUID())
                    .setMaxAge(COOKIE_MAX_AGE)
                    .setSecure(true)
                    .setHttpOnly(false)
                    .setPath("/")
                    .setSameSite(CookieSameSite.NONE);
            ctx.request().response().addCookie(cookie);
        }
        ctx.next();
    }

}
