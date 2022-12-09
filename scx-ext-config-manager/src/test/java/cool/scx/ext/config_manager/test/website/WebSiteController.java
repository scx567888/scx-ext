package cool.scx.ext.config_manager.test.website;

import cool.scx.core.ScxContext;
import cool.scx.core.annotation.ScxMapping;
import cool.scx.core.enumeration.HttpMethod;
import cool.scx.core.vo.Json;
import cool.scx.ext.config_manager.test.impl.ConfigManager;

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
    public Json index() {
        ConfigManager bean = ScxContext.getBean(ConfigManager.class);
        return Json.ok()
                .put("system-config",bean.getSystemConfig())
                .put("user-config",bean.getUserConfig(1L));
    }

}
