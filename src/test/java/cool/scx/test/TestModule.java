package cool.scx.test;

import cool.scx.Scx;
import cool.scx.ScxModule;
import cool.scx.enumeration.ScxFeature;
import cool.scx.ext.cms.CMSModule;
import cool.scx.ext.core.CoreModule;
import cool.scx.ext.crud.CRUDModule;
import cool.scx.ext.fixtable.FixTableModule;
import cool.scx.ext.fss.FSSModule;
import cool.scx.ext.organization.OrganizationModule;
import cool.scx.test.chat_room.ChatRoomHandler;
import cool.scx.test.cms.UserListWebSiteHandler;

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
                        new CMSModule().setWebSiteHandler(UserListWebSiteHandler.class),
                        new CoreModule(),
                        new CRUDModule(),
                        new FixTableModule(),
                        new FSSModule(),
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
        ChatRoomHandler.registerAllHandler();
    }

}
