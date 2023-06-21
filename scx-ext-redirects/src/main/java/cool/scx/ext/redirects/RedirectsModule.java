package cool.scx.ext.redirects;

import cool.scx.core.Scx;
import cool.scx.core.ScxModule;
import cool.scx.mvc.vo.Redirections;
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
     *
     * @param vertx a {@link io.vertx.core.Vertx} object
     * @param port  a int
     */
    public static void startRedirects(Vertx vertx, int port) {
        var router = Router.router(vertx);
        router.route().handler(c -> {
            var oldURI = c.request().absoluteURI();
            // 4 = "http".length()
            var newURI = "https" + oldURI.substring(4);
            Redirections.ofTemporary(newURI).accept(c);
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
    public void start(Scx scx) {
        //只有当开启 https 的时候才进行转发
        if (scx.scxOptions().isHttpsEnabled()) {
            startRedirects(scx.vertx(), this.port);
        }
    }

}