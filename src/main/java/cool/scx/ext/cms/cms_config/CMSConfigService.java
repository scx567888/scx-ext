package cool.scx.ext.cms.cms_config;

import cool.scx.annotation.ScxService;
import cool.scx.base.BaseModelService;
import cool.scx.base.Query;

/**
 * <p>CmsConfigService class.</p>
 *
 * @author scx567888
 * @version 1.3.9
 */
@ScxService
public class CMSConfigService extends BaseModelService<CMSConfig> {

    /**
     * <p>getCmsConfig.</p>
     *
     * @return a {@link cool.scx.ext.cms.cms_config.CMSConfig} object
     */
    public CMSConfig getCMSConfig() {
        //这里以 id 为 1 的数据作为标准
        var cmsConfig = get(new Query().equal("configName", "defaultConfig"));
        if (cmsConfig != null) {
            return cmsConfig;
        }
        var c = new CMSConfig();
        c.configName = "defaultConfig";
        return add(c);
    }

}
