package cool.scx.ext.static_server.test.website;

import cool.scx.core.annotation.ScxMapping;
import cool.scx.core.enumeration.HttpMethod;
import cool.scx.core.vo.Html;

import java.io.IOException;

/**
 * 简单测试
 *
 * @author scx567888
 * @version 0.3.6
 * @since 1.3.14
 */
@ScxMapping("/")
public class WebSiteController {

    @ScxMapping(value = "/", method = HttpMethod.GET)
    public static Html index() throws IOException {
        return Html.of("index");
    }

}
