package cool.scx.ext.cms.web_site;

import cool.scx.core.ScxContext;
import cool.scx.enumeration.HttpMethod;
import cool.scx.ext.cms.CMSModule;
import cool.scx.ext.cms.channel.ChannelService;
import cool.scx.ext.cms.cms_config.CMSConfigService;
import cool.scx.ext.cms.content.ContentService;
import cool.scx.mvc.annotation.FromPath;
import cool.scx.mvc.annotation.ScxRoute;
import cool.scx.mvc.exception.NotFoundException;
import cool.scx.mvc.vo.Html;

import java.io.IOException;

/**
 * <p>WebSiteController class.</p>
 *
 * @author scx567888
 * @version 1.3.9
 */
@ScxRoute("")
public class WebSiteController {

    private final ContentService contentService;
    private final ChannelService channelService;
    private final CMSConfigService cmsConfigService;
    private final WebSiteHandler webSiteHandler;

    public WebSiteController(ContentService contentService, ChannelService channelService, CMSConfigService cmsConfigService) {
        this.contentService = contentService;
        this.channelService = channelService;
        this.cmsConfigService = cmsConfigService;
        var webSiteHandlerClass = ScxContext.findScxModule(CMSModule.class).getWebSiteHandlerClass();
        this.webSiteHandler = ScxContext.getBean(webSiteHandlerClass);
    }

    @ScxRoute(value = "", methods = HttpMethod.GET)
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

    @ScxRoute(value = "/:channelPath", methods = HttpMethod.GET)
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

    @ScxRoute(value = "/:channelPath/:contentID", methods = HttpMethod.GET)
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
