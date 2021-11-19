package cool.scx.test;

import cool.scx.Scx;
import cool.scx.ScxConstant;
import cool.scx.ScxContext;
import cool.scx.ScxModule;
import cool.scx.enumeration.ScxFeature;
import cool.scx.ext.cms.CMSModule;
import cool.scx.ext.core.CoreModule;
import cool.scx.ext.core.CoreOnlineItemHandler;
import cool.scx.ext.core.WSBody;
import cool.scx.ext.crud.CRUDModule;
import cool.scx.ext.fixtable.FixTableModule;
import cool.scx.ext.fss.FSSModule;
import cool.scx.ext.message.MessageModule;
import cool.scx.ext.office.OfficeModule;
import cool.scx.ext.organization.OrganizationModule;

import java.time.LocalDateTime;

/**
 * <p>TestModule class.</p>
 *
 * @author scx567888
 * @version 1.3.14
 * @since 1.3.14
 */
public class TestModule implements ScxModule {

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects
     */
    public static void main(String[] args) {
        Scx.builder()
                .setMainClass(TestModule.class)
                .configure(ScxFeature.USE_DEVELOPMENT_ERROR_PAGE, true)
                .addModules(
                        new CMSModule().setWebSiteHandler(TestUserListWebSiteHandler.class),
                        new CoreModule(),
                        new CRUDModule(),
                        new FixTableModule(),
                        new FSSModule(),
                        new MessageModule(),
                        new OfficeModule(),
                        new OrganizationModule(),
                        new TestModule())
                .setArgs(args)
                .build().run();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        //注册事件
        ScxContext.eventBus().consumer("sendMessage", (m) -> SendMessageHandler.sendMessage((WSBody) m));
        ScxContext.scheduleAtFixedRate(() -> {
            var onlineItemList = CoreOnlineItemHandler.getOnlineItemList();
            for (var onlineItem : onlineItemList) {
                onlineItem.send(new WSBody("writeTime", ScxConstant.DEFAULT_DATETIME_FORMATTER.format(LocalDateTime.now()), null).toJson());
            }
        }, 0, 1000);

    }
}
