package cool.scx.ext.fss.test.website;

import cool.scx.core.annotation.ScxMapping;
import cool.scx.core.enumeration.HttpMethod;

/**
 * 简单测试
 *
 * @author scx567888
 * @version 0.3.6
 * @since 1.3.14
 */
@ScxMapping("/")
public class WebSiteController {

    @ScxMapping(method = HttpMethod.GET, useNameAsUrl = false)
    public static String index() {
        return "hello";
    }

}
