package cool.scx.ext.static_server;

import cool.scx.config.ScxConfigValueHandler;
import cool.scx.config.ScxEnvironment;
import cool.scx.config.handler.DefaultValueHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

record ConvertStaticServerHandler(
        ScxEnvironment scxEnvironment) implements ScxConfigValueHandler<List<StaticServer>> {

    @Override
    public List<StaticServer> handle(String keyPath, Object rawValue) {
        var arrayList = DefaultValueHandler.of(new ArrayList<Map<String, String>>()).handle(keyPath, rawValue);
        var tempList = new ArrayList<StaticServer>();
        for (var arg : arrayList) {
            try {
                tempList.add(new StaticServer(arg.get("location"), scxEnvironment.getPathByAppRoot(arg.get("root"))));
            } catch (Exception ignored) {

            }
        }
        return tempList;
    }

}
