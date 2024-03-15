package cool.scx.ext.static_server;

import cool.scx.common.util.ScxExceptionHelper;
import cool.scx.core.Scx;
import cool.scx.core.ScxModule;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.FileSystemAccess;
import io.vertx.ext.web.handler.StaticHandler;

import java.lang.System.Logger;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.System.Logger.Level.DEBUG;

public class StaticServerModule extends ScxModule {

    private static final Logger logger = System.getLogger(StaticServerModule.class.getName());

    private static void registerStaticServerHandler(Router vertxRouter, List<StaticServer> staticServers) {
        for (var staticServer : staticServers) {
            var isRegularFile = ScxExceptionHelper.ignore(() -> Files.isRegularFile(staticServer.root()), true);
            if (isRegularFile) {// 单文件的 (使用场景例如 vue-router history 模式)
                vertxRouter.route(staticServer.location())
                        .handler(new SingleFileStaticHandler(staticServer.root()));
            } else {
                // 目录则采用 vertx 自带的
                vertxRouter.route(staticServer.location())
                        .handler(StaticHandler.create(FileSystemAccess.ROOT, staticServer.root().toString())
                                .setFilesReadOnly(false));
            }
        }
    }


    @Override
    public void start(Scx scx) {
        var staticServers = scx.scxConfig().get("static-servers", new ConvertStaticServerHandler(scx.scxEnvironment()));
        logger.log(DEBUG, "静态资源服务器 -->  {0}", staticServers.stream().map(StaticServer::location).collect(Collectors.joining(", ", "[", "]")));
        registerStaticServerHandler(scx.scxHttpRouter(), staticServers);
    }

    @Override
    public String name() {
        return "SCX_EXT-" + super.name();
    }

}
