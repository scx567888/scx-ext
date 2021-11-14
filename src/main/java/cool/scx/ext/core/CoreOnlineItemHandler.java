package cool.scx.ext.core;

import io.vertx.core.http.ServerWebSocket;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>CoreOnlineItemHandler class.</p>
 *
 * @author scx567888
 * @version 1.3.5
 */
public class CoreOnlineItemHandler {
    /**
     * 存储所有在线的 连接
     */
    private static final List<CoreOnlineItem> ONLINE_ITEMS = new ArrayList<>();

    /**
     * <p>addOnlineItem.</p>
     *
     * @param webSocket a {@link io.vertx.core.http.ServerWebSocket} object.
     * @param userID    a {@link java.lang.String} object.
     */
    public static void addOnlineItem(ServerWebSocket webSocket, Long userID) {
        var binaryHandlerID = webSocket.binaryHandlerID();
        //看看这个相对应的连接 是不是 已经注册到 ONLINE_ITEMS 中了 如果已经存在 就不重写注册了 而是直接更新 username
        //有点像  HashMap 的逻辑
        var onlineItem = ONLINE_ITEMS.stream().filter(u ->
                u.webSocket.binaryHandlerID().equals(binaryHandlerID)).findAny().orElse(null);
        if (onlineItem == null) {
            //先生成一个 在线用户的对象
            var newOnlineItem = new CoreOnlineItem(webSocket, userID);
            ONLINE_ITEMS.add(newOnlineItem);
        } else {
            onlineItem.userID = userID;
        }
    }

    /**
     * <p>removeOnlineItemByWebSocket.</p>
     *
     * @param webSocket a {@link io.vertx.core.http.ServerWebSocket} object.
     * @return a boolean.
     */
    public static boolean removeOnlineItemByWebSocket(ServerWebSocket webSocket) {
        return ONLINE_ITEMS.removeIf(f -> f.webSocket.binaryHandlerID().equals(webSocket.binaryHandlerID()));
    }

    /**
     * <p>getOnlineUserCount.</p>
     *
     * @return a long.
     */
    public static long getOnlineUserCount() {
        return ONLINE_ITEMS.stream().filter(u -> u.userID != null).count();
    }

    /**
     * <p>getOnlineItemByWebSocket.</p>
     *
     * @param webSocket a {@link io.vertx.core.http.ServerWebSocket} object.
     * @return a {@link cool.scx.ext.core.CoreOnlineItem} object.
     */
    public static CoreOnlineItem getOnlineItemByWebSocket(ServerWebSocket webSocket) {
        var binaryHandlerID = webSocket.binaryHandlerID();
        return ONLINE_ITEMS.stream().filter(u -> u.webSocket.binaryHandlerID().equals(binaryHandlerID)).findAny().orElse(null);
    }

    /**
     * 根据用户名获取所有的在线对象
     *
     * @param userID a {@link java.lang.String} object.
     * @return a {@link cool.scx.ext.core.CoreOnlineItem} object.
     */
    public static CoreOnlineItem getOnlineItemByUserID(Long userID) {
        return ONLINE_ITEMS.stream().filter(u -> u.userID.equals(userID)).findAny().orElse(null);
    }

    /**
     * 获取当前所有在线的连接对象
     *
     * @return 当前所有在线的连接对象
     */
    public static List<CoreOnlineItem> getOnlineItemList() {
        return ONLINE_ITEMS;
    }
}
