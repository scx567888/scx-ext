package cool.scx.ext.fix_table;

import cool.scx.core.Scx;
import cool.scx.core.ScxModule;
import cool.scx.util.ConsoleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>FixTableModule class.</p>
 *
 * @author scx567888
 * @version 1.3.0
 */
public class FixTableModule extends ScxModule {

    /**
     * Constant <code>logger</code>
     */
    private static final Logger logger = LoggerFactory.getLogger(FixTableModule.class);

    /**
     * a
     *
     * @return a
     */
    private static boolean confirmFixTable() {
        while (true) {
            var errMessage = """
                    *******************************************************
                    *                                                     *
                    *           Y 检测到需要修复数据表 , 是否修复 ?             *
                    *                                                     *
                    *         [Y]修复数据表  |  [N]忽略  |  [Q]退出           *
                    *                                                     *
                    *******************************************************
                    """;
            System.err.println(errMessage);
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
    public void start(Scx scx) {
        scx.scxScheduler().submit(() -> {
            if (!scx.checkDataSource()) {
                logger.error("数据源连接失败!!! 已跳过修复表!!!");
                return;
            }
            if (scx.checkNeedFixTable()) {
                if (confirmFixTable()) {
                    scx.fixTable();
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
        return "SCX_EXT-" + super.name();
    }

}