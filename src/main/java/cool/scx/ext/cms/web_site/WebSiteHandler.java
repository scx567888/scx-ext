package cool.scx.ext.cms.web_site;

import cool.scx.vo.Html;

/**
 * <p>WebSiteHandler interface.</p>
 *
 * @author scx567888
 * @version 1.3.9
 */
public interface WebSiteHandler {

    /**
     * cms 访问 index 会执行此方法
     *
     * @param indexTemplate a {@link cool.scx.vo.Html} object
     * @throws Exception s
     */
    default void indexHandler(Html indexTemplate) throws Exception {

    }

    /**
     * cms 访问 栏目是会执行此方法
     *
     * @param channelTemplate a {@link cool.scx.vo.Html} object
     * @param channelPath     a {@link java.lang.String} object
     * @throws Exception s
     */
    default void channelHandler(Html channelTemplate, String channelPath) throws Exception {

    }

    /**
     * cms 访问 文章时会执行此方法
     *
     * @param contentTemplate a
     * @param channelPath     a
     * @param contentID       a
     * @throws Exception s
     */
    default void contentHandler(Html contentTemplate, String channelPath, Long contentID) throws Exception {

    }

}
