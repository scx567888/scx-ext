package cool.scx.ext.redirects;

import cool.scx.core.Scx;
import cool.scx.core.ScxModule;
import cool.scx.mvc.vo.Redirections;
import cool.scx.util.ansi.Ansi;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

import java.lang.System.Logger;

/**
 * 监听 80 端口并将所有 http 请求重定向 到 https
 *
 * @author scx567888
 * @version 1.15.2
 */
public class RedirectsModule extends ScxModule {

    private static final Logger logger = System.getLogger(RedirectsModule.class.getName());

    private final int port;

    public RedirectsModule() {
        this(80);
    }

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
                logger.log(Logger.Level.ERROR, "转发服务器启动失败 !!! ", http.cause());
            }
        });
    }

    @Override
    public String name() {
        return "SCX_EXT-" + super.name();
    }

    @Override
    public void start(Scx scx) {
        //只有当开启 https 的时候才进行转发
        if (scx.scxOptions().isHttpsEnabled()) {
            startRedirects(scx.vertx(), this.port);
        }
    }

}
