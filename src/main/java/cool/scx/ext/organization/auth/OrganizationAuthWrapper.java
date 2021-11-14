package cool.scx.ext.organization.auth;

/**
 *
 */
public final class OrganizationAuthWrapper {

    private static boolean initFlag = false;

    public static void initAuth() {
        if (initFlag) {
            System.err.println("不能多次进行初始化");
            return;
        }
        OrganizationAuth.initAuth();
        //修改初始化 flag
        initFlag = true;
    }

    public static void readSessionFromFile() {
        OrganizationAuth.readSessionFromFile();
    }

    public static void writeSessionToFile() {
        OrganizationAuth.writeSessionToFile();
    }

}