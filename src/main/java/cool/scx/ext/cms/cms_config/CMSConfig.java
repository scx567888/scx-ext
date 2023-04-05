package cool.scx.ext.cms.cms_config;

import cool.scx.core.base.BaseModel;
import cool.scx.data.annotation.Column;
import cool.scx.data.annotation.Table;
import cool.scx.ext.crud.annotation.UseCRUDApi;

/**
 * <p>CmsConfig class.</p>
 *
 * @author scx567888
 * @version 1.3.9
 */
@UseCRUDApi
@Table(tablePrefix = "cms")
public class CMSConfig extends BaseModel {

    /**
     * 配置文件名称
     */
    @Column(unique = true)
    public String configName;

    /**
     * 默认 index 模板 存储的是相对于模板根路径的路径
     * xxx/xxx (这就属于以当前项目根目录为基准)
     * 栏目模板和文章模板 同理
     */
    @Column(defaultValue = "'index'")
    public String defaultIndexTemplate;

    /**
     * 默认 栏目 模板
     */
    @Column(defaultValue = "'channel_template/DefaultChannelTemplate'")
    public String defaultChannelTemplate;

    /**
     * 默认 文章 模板
     */
    @Column(defaultValue = "'content_template/DefaultContentTemplate'")
    public String defaultContentTemplate;

}
