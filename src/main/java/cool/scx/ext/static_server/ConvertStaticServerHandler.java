package cool.scx.ext.static_server;

import cool.scx.ScxEnvironment;
import cool.scx.ScxHandlerR;
import cool.scx.config.handler.ScxConfigHandlerParam;
import cool.scx.config.handler.impl.DefaultValueHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * a
 */
record ConvertStaticServerHandler(
        ScxEnvironment scxEnvironment) implements ScxHandlerR<ScxConfigHandlerParam, List<StaticServer>> {

    @Override
    public List<StaticServer> handle(ScxConfigHandlerParam o) {
        var arrayList = new DefaultValueHandler<>(new ArrayList<Map<String, String>>()).handle(o);
        var tempList = new ArrayList<StaticServer>();
        for (var arg : arrayList) {
            try {
                tempList.add(new StaticServer(arg.get("location"), scxEnvironment.getFileByAppRoot(arg.get("root"))));
            } catch (Exception ignored) {

            }
        }
        return tempList;
    }

}
