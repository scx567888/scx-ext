package cool.scx.ext.fixtable;

import cool.scx.ScxContext;
import cool.scx.ScxModule;
import cool.scx.dao.ScxDaoHelper;
import cool.scx.util.ConsoleUtils;
import org.slf4j.LoggerFactory;

/**
 * <p>FixTableModule class.</p>
 *
 * @author scx567888
 * @version 1.3.0
 */
public class FixTableModule implements ScxModule {

    /**
     * a
     *
     * @return a
     */
    private static boolean confirmFixTable() {
        while (true) {
            System.err.println("Y 检测到需要修复数据表 , 是否修复? [Y]修复数据表 [N]忽略 [Q]退出 ");
            var result = ConsoleUtils.readLine().trim();
            if ("Y".equalsIgnoreCase(result)) {
                return true;
            } else if ("N".equalsIgnoreCase(result)) {
                return false;
            } else if ("Q".equalsIgnoreCase(result)) {
                System.exit(-1);
                return false;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        var logger = LoggerFactory.getLogger(FixTableModule.class);
        ScxContext.execute(() -> {
            if (!ScxContext.dao().checkDataSource()) {
                logger.error("数据源连接失败!!! 已跳过修复表!!!");
                return;
            }
            if (ScxDaoHelper.checkNeedFixTable()) {
                if (confirmFixTable()) {
                    ScxDaoHelper.fixTable();
                } else {
                    logger.debug("用户已取消修复表 !!!");
                }
            } else {
                logger.debug("没有表需要修复...");
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "SCX_EXT-" + ScxModule.super.name();
    }

}
