package cool.scx.ext.cms.channel;

import cool.scx.core.annotation.Column;
import cool.scx.core.annotation.ScxModel;
import cool.scx.core.base.BaseModel;
import cool.scx.ext.crud.annotation.UseCRUDApi;

import java.util.List;

/**
 * <p>Column class.</p>
 *
 * @author scx567888
 * @version 0.3.6
 */
@UseCRUDApi
@ScxModel(tablePrefix = "cms")
public class Channel extends BaseModel {

    /**
     * 栏目标题图 (缩略图)
     */
    public String channelTitleImage;

    /**
     * 栏目图片 (用于展示)
     */
    public List<String> channelImages;

    /**
     * 栏目父 ID
     */
    public Long parentID;

    /**
     * 栏目介绍
     */
    public String channelInfo;

    /**
     * 栏目名称
     */
    public String channelName;

    /**
     * 栏目路径
     */
    @Column(unique = true)
    public String channelPath;

    /**
     * 栏目 模板
     */
    public String channelTemplate;

    /**
     * 栏目 order (用于排序)
     */
    public Long channelOrder;

}
