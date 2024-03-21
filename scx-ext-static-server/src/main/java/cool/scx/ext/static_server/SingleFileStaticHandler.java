package cool.scx.ext.static_server;

import io.vertx.core.file.FileSystem;
import io.vertx.ext.web.RoutingContext;

import java.nio.file.Path;

class SingleFileStaticHandler extends StaticHandlerImpl {

    private final String singleFile;

    public SingleFileStaticHandler(Path root) {
        this.singleFile = root.toString();
    }

    @Override
    public String getFile(String path, RoutingContext context) {
        return singleFile;
    }

    @Override
    public void sendStatic(RoutingContext context, FileSystem fileSystem, String path, boolean index) {
        super.sendStatic(context, fileSystem, path, false);
    }

}
