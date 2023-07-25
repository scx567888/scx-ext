package cool.scx.ext.cms.cms_config;

import cool.scx.core.annotation.ScxService;
import cool.scx.core.base.BaseModelService;

import static cool.scx.data.Query.query;
import static cool.scx.data.query.WhereBody.equal;

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
        var cmsConfig = get(query().where(equal("configName", "defaultConfig")));
        if (cmsConfig != null) {
            return cmsConfig;
        }
        var c = new CMSConfig();
        c.configName = "defaultConfig";
        return add(c);
    }

}
