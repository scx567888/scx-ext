package cool.scx.ext.cms.web_site;

import cool.scx.core.ScxContext;
import cool.scx.core.annotation.FromPath;
import cool.scx.core.annotation.ScxMapping;
import cool.scx.core.enumeration.HttpMethod;
import cool.scx.core.http.exception.impl.NotFoundException;
import cool.scx.core.vo.Html;
import cool.scx.ext.cms.CMSModule;
import cool.scx.ext.cms.channel.ChannelService;
import cool.scx.ext.cms.cms_config.CMSConfigService;
import cool.scx.ext.cms.content.ContentService;

import java.io.IOException;

/**
 * <p>WebSiteController class.</p>
 *
 * @author scx567888
 * @version 1.3.9
 */
@ScxMapping("/")
public class WebSiteController {

    private final ContentService contentService;
    private final ChannelService channelService;
    private final CMSConfigService cmsConfigService;
    private final WebSiteHandler webSiteHandler;

    /**
     * <p>Constructor for WebSiteController.</p>
     *
     * @param contentService   a {@link cool.scx.ext.cms.content.ContentService} object
     * @param channelService   a {@link cool.scx.ext.cms.channel.ChannelService} object
     * @param cmsConfigService a {@link cool.scx.ext.cms.cms_config.CMSConfigService} object
     */
    public WebSiteController(ContentService contentService, ChannelService channelService, CMSConfigService cmsConfigService) {
        this.contentService = contentService;
        this.channelService = channelService;
        this.cmsConfigService = cmsConfigService;
        var webSiteHandlerClass = ScxContext.findScxModule(CMSModule.class).getWebSiteHandlerClass();
        this.webSiteHandler = ScxContext.getBean(webSiteHandlerClass);
    }

    /**
     * <p>index.</p>
     *
     * @return a {@link cool.scx.core.vo.Html} object
     * @throws java.lang.Exception if any.
     */
    @ScxMapping(value = "/", method = HttpMethod.GET)
    public Html index() throws Exception {
        var cmsConfig = cmsConfigService.getCMSConfig();
        var indexTemplatePath = cmsConfig.defaultIndexTemplate;
        Html html;
        try {
            html = Html.of(indexTemplatePath);
        } catch (IOException e) {//模板物理文件丢失了
            throw new NotFoundException();
        }
        //执行 webSiteHandler
        webSiteHandler.indexHandler(html);
        return html;
    }

    /**
     * <p>column.</p>
     *
     * @param channelPath a {@link java.lang.String} object
     * @return a {@link cool.scx.core.vo.Html} object
     * @throws java.lang.Exception if any.
     */
    @ScxMapping(value = "/:channelPath", method = HttpMethod.GET)
    public Html channel(@FromPath String channelPath) throws Exception {
        var channelByPath = channelService.getChannelByPath(channelPath);
        if (channelByPath == null) {
            throw new NotFoundException();
        }
        var cmsConfig = cmsConfigService.getCMSConfig();
        var defaultChannelTemplate = cmsConfig.defaultChannelTemplate;
        if (channelByPath.channelTemplate != null) {
            defaultChannelTemplate = channelByPath.channelTemplate;
        }

        Html html;
        try {
            html = Html.of(defaultChannelTemplate);
        } catch (IOException e) {//模板物理文件丢失了
            throw new NotFoundException();
        }
        //执行 webSiteHandler
        webSiteHandler.channelHandler(html, channelPath);
        //将当前的栏目放到页面中去
        html.add("channel", channelByPath);
        return html;
    }

    /**
     * <p>article.</p>
     *
     * @param channelPath a {@link java.lang.String} object
     * @param contentID   a {@link java.lang.Long} object
     * @return a {@link cool.scx.core.vo.Html} object
     * @throws java.lang.Exception if any.
     */
    @ScxMapping(value = "/:channelPath/:contentID", method = HttpMethod.GET)
    public Html content(@FromPath String channelPath, @FromPath Long contentID) throws Exception {
        //栏目为空直接 404
        var channelByPath = channelService.getChannelByPath(channelPath);
        if (channelByPath == null) {
            throw new NotFoundException();
        }
        //文章为空或者文章所属的栏目id和当前栏目的id不否 404
        var content = contentService.get(contentID);
        if (content == null || !content.channelID.equals(channelByPath.id)) {
            throw new NotFoundException();
        }
        var cmsConfig = cmsConfigService.getCMSConfig();
        var defaultContentTemplate = cmsConfig.defaultContentTemplate;
        if (content.contentTemplate != null) {
            defaultContentTemplate = content.contentTemplate;
        }
        Html html;
        try {
            html = Html.of(defaultContentTemplate);
        } catch (IOException e) {//模板物理文件丢失了
            throw new NotFoundException();
        }
        //执行 webSiteHandler
        webSiteHandler.contentHandler(html, channelPath, contentID);
        //将当前的栏目和文章放到页面中去
        html.add("channel", channelByPath);
        html.add("content", content);
        return html;
    }

}
