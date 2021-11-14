package cool.scx.ext.core;

import cool.scx.annotation.ScxService;
import cool.scx.ext.organization.auth.OrganizationAuth;
import cool.scx.util.ansi.Ansi;

/**
 * 通过 websocket 认证 token
 *
 * @author scx567888
 * @version 1.1.17
 */
@ScxService
public class CoreAuthLoginHandler {

    /**
     * <p>loginByWebSocket.</p>
     *
     * @param o a {@link java.lang.Object} object
     */
    public static void loginByWebSocket(Object o) {
        var wsBody = (WSBody) o;
        var token = wsBody.data().get("token").asText();
        if (token != null) {
            Ansi.out().green(token).println();
            var loginUserByToken = OrganizationAuth.getLoginUserByToken(token);
            //这条websocket 连接验证通过
            if (loginUserByToken != null) {
                CoreOnlineItemHandler.addOnlineItem(wsBody.webSocket(), loginUserByToken.id);
                Ansi.out().brightGreen(wsBody.webSocket().binaryHandlerID() + " 登录了!!! 登录用户 ID : " + loginUserByToken.id + " ,用户名 : " + loginUserByToken.username).println();
                Ansi.out().brightYellow("当前总在线用户数量 : " + CoreOnlineItemHandler.getOnlineUserCount()).println();
            }
        }
    }

}
