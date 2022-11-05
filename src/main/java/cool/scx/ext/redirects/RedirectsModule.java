package cool.scx.ext.redirects;

import cool.scx.core.ScxContext;
import cool.scx.core.ScxModule;
import cool.scx.core.vo.Redirections;
import cool.scx.util.ansi.Ansi;
import io.vertx.ext.web.Router;

/**
 * 监听 80 端口并将所有 http 请求重定向 到 https
 *
 * @author scx567888
 * @version 1.15.2
 */
public class RedirectsModule extends ScxModule {

    private final int port;

    public RedirectsModule() {
        this(80);
    }

    public RedirectsModule(int port) {
        this.port = port;
    }

    @Override
    public String name() {
        return "SCX_EXT-" + super.name();
    }

    @Override
    public void start() {
        init();
    }

    private void init() {
        var router = Router.router(ScxContext.vertx());
        router.route().handler(c -> {
            var oldURI = c.request().absoluteURI();
            var newURI = "https" + oldURI.substring("http".length());
            Redirections.ofTemporary(newURI).handle(c);
        });
        ScxContext.vertx().createHttpServer().requestHandler(router).listen(port, (http) -> {
            if (http.succeeded()) {
                Ansi.out().brightMagenta("转发服务器启动成功 http -> https, 端口号 : " + port + " !!!").println();
            } else {
                http.cause().printStackTrace();
            }
        });
    }

}
