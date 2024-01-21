package cool.scx.ext.cms.content;

import cool.scx.core.base.BaseModel;
import cool.scx.data.jdbc.annotation.Column;
import cool.scx.data.jdbc.annotation.Table;
import cool.scx.ext.crud.annotation.UseCRUDApi;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章类
 *
 * @author scx567888
 * @version 0.3.6
 */
@UseCRUDApi
@Table(tablePrefix = "cms")
public class Content extends BaseModel {

    /**
     * 文章标题图 (缩略图)
     */
    public String contentTitleImage;

    /**
     * 文章 图片 (不是内容中的图 展示图)
     */
    public List<String> contentImages;

    /**
     * 文章标题
     */
    public String contentTitle;

    /**
     * 文章内容
     */
    @Column(dataType = "TEXT")
    public String content;

    /**
     * 对应的 栏目 id
     */
    @Column(notNull = true)
    public Long channelID;

    /**
     * 文章模板路径
     */
    public String contentTemplate;

    /**
     * 文章 order (用于排序)
     */
    public Long contentOrder;

    /**
     * 文章作者
     */
    public String contentAuthor;

    /**
     * 文章发布日期
     */
    public LocalDateTime contentPublishDateTime;

}
