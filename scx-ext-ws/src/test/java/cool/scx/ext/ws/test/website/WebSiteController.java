package cool.scx.ext.ws.test.website;

import cool.scx.core.annotation.ScxMapping;
import cool.scx.core.enumeration.HttpMethod;
import cool.scx.core.vo.Html;

import java.io.IOException;

@ScxMapping
public class WebSiteController {

    @ScxMapping(value = "/",method = HttpMethod.GET)
    public static Html index() throws IOException {
        return Html.of("index");
    }

}
