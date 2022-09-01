package cool.scx.ext.static_server;

import cool.scx.core.ScxContext;
import cool.scx.core.ScxModule;
import cool.scx.util.ScxExceptionHelper;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.FileSystemAccess;
import io.vertx.ext.web.handler.StaticHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

/**
 * a
 *
 * @author scx567888
 * @version 1.10.8
 */
public class StaticServerModule extends ScxModule {

    /**
     * Constant <code>logger</code>
     */
    private static final Logger logger = LoggerFactory.getLogger(StaticServerModule.class);

    /**
     * a
     *
     * @return a
     */
    private static List<StaticServer> getStaticServersByConfig() {
        return ScxContext.config().get("static-servers", new ConvertStaticServerHandler(ScxContext.environment()));
    }

    /**
     * a
     *
     * @param vertxRouter   a
     * @param staticServers a
     */
    private static void registerStaticServerHandler(Router vertxRouter, List<StaticServer> staticServers) {
        for (var staticServer : staticServers) {
            var isRegularFile = ScxExceptionHelper.ignore(() -> Files.isRegularFile(staticServer.root()), true);
            if (isRegularFile) {// 单文件的 (使用场景例如 vue-router history 模式)
                vertxRouter.route(staticServer.location())
                        .handler(new SingleFileStaticHandlerImpl(staticServer.root()));
            } else {
                // 目录则采用 vertx 自带的
                vertxRouter.route(staticServer.location())
                        .handler(StaticHandler.create(FileSystemAccess.ROOT, staticServer.root().toString())
                                .setFilesReadOnly(false));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        var staticServers = getStaticServersByConfig();
        logger.info("静态资源服务器 -->  {}", staticServers.stream().map(StaticServer::location).collect(Collectors.joining(", ", "[", "]")));
        registerStaticServerHandler(ScxContext.router().vertxRouter(), staticServers);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "SCX_EXT-" + super.name();
    }

}
