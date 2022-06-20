package cool.scx.ext.organization.auth;

import cool.scx.util.RandomUtils;
import io.vertx.core.Handler;
import io.vertx.core.http.impl.CookieImpl;
import io.vertx.ext.web.RoutingContext;

import static cool.scx.ext.organization.auth.ScxAuth.SCX_AUTH_TOKEN_KEY;

final class ScxAuthCookieHandler implements Handler<RoutingContext> {

    private static final long COOKIE_MAX_AGE = 1800000L;

    @Override
    public void handle(RoutingContext ctx) {
        if (ctx.request().getCookie(SCX_AUTH_TOKEN_KEY) == null) {
            ctx.request().response().addCookie(new CookieImpl(SCX_AUTH_TOKEN_KEY, RandomUtils.getUUID()).setMaxAge(COOKIE_MAX_AGE));
        }
        ctx.next();
    }

}