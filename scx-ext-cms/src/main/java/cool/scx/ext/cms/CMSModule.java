package cool.scx.ext.cms;

import cool.scx.core.Scx;
import cool.scx.core.ScxContext;
import cool.scx.core.ScxModule;
import cool.scx.ext.cms.directive.ChannelListDirective;
import cool.scx.ext.cms.directive.ContentListDirective;
import cool.scx.ext.cms.web_site.WebSiteHandler;
import cool.scx.ext.cms.web_site.WebSiteHandlerImpl;
import cool.scx.mvc.base.BaseTemplateDirective;

import java.util.ArrayList;
import java.util.List;

/**
 * cms 模块 提供一些简单的 指令
 *
 * @author scx567888
 * @version 1.1.11
 */
public class CMSModule extends ScxModule {

    /**
     * 指令
     */
    private final List<Class<? extends BaseTemplateDirective>> userDirectiveList = new ArrayList<>();

    /**
     * WebSiteHandler 列表 访问页面时会按照顺序执行 一般用来向首页添加自定义的信息
     */
    private Class<? extends WebSiteHandler> webSiteHandlerClass = WebSiteHandlerImpl.class;

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Scx scx) {
        //添加默认指令和用户指令到 模板指令中
        for (var aClass : this.defaultDirective()) {
            ScxContext.scxMvc().templateHandler().addDirective(scx.beanFactory().getBean(aClass));
        }
        for (var aClass : this.getUserDirective()) {
            ScxContext.scxMvc().templateHandler().addDirective(scx.beanFactory().getBean(aClass));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "SCX_EXT-" + super.name();
    }

    /**
     * <p>Getter for the field <code>webSiteHandlerClass</code>.</p>
     *
     * @return a
     */
    public Class<? extends WebSiteHandler> getWebSiteHandlerClass() {
        return webSiteHandlerClass;
    }

    /**
     * <p>setWebSiteHandler.</p>
     *
     * @param webSiteHandlerClass a
     * @return s
     */
    public CMSModule setWebSiteHandler(Class<? extends WebSiteHandler> webSiteHandlerClass) {
        this.webSiteHandlerClass = webSiteHandlerClass;
        return this;
    }

    /**
     * 获取默认指令
     *
     * @param userDirectiveClass a
     * @return r
     */
    public CMSModule addUserDirective(Class<? extends BaseTemplateDirective> userDirectiveClass) {
        this.userDirectiveList.add(userDirectiveClass);
        return this;
    }

    /**
     * 获取默认指令
     *
     * @return r
     */
    public List<Class<? extends BaseTemplateDirective>> getUserDirective() {
        return userDirectiveList;
    }

    /**
     * 获取默认指令
     *
     * @return r
     */
    public List<Class<? extends BaseTemplateDirective>> defaultDirective() {
        return List.of(ContentListDirective.class, ChannelListDirective.class);
    }

}
