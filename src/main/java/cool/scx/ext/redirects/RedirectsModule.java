package cool.scx.ext.redirects;

import cool.scx.core.ScxContext;
import cool.scx.core.ScxModule;
import cool.scx.core.vo.Redirections;
import cool.scx.util.ansi.Ansi;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

/**
 * 监听 80 端口并将所有 http 请求重定向 到 https
 *
 * @author scx567888
 * @version 1.15.2
 */
public class RedirectsModule extends ScxModule {

    private final int port;

    /**
     * <p>Constructor for RedirectsModule.</p>
     */
    public RedirectsModule() {
        this(80);
    }

    /**
     * <p>Constructor for RedirectsModule.</p>
     *
     * @param port a int
     */
    public RedirectsModule(int port) {
        this.port = port;
    }

    /**
     * 也可以直接以工具类的形式调用
     */
    public static void startRedirects(Vertx vertx, int port) {
        var router = Router.router(vertx);
        router.route().handler(c -> {
            var oldURI = c.request().absoluteURI();
            var newURI = "https" + oldURI.substring("http".length());
            Redirections.ofTemporary(newURI).handle(c);
        });
        vertx.createHttpServer().requestHandler(router).listen(port, (http) -> {
            if (http.succeeded()) {
                Ansi.out().brightMagenta("转发服务器启动成功 http -> https, 端口号 : " + port + " !!!").println();
            } else {
                http.cause().printStackTrace();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "SCX_EXT-" + super.name();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        //只有当开启 https 的时候才进行转发
        if (ScxContext.coreConfig().isHttpsEnabled()) {
            startRedirects(ScxContext.vertx(), this.port);
        }
    }

}
