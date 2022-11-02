package cool.scx.ext.organization.auth;

import io.vertx.core.http.ServerWebSocket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>AlreadyLoginClientMap class.</p>
 *
 * @author scx567888
 * @version 1.11.8
 */
public final class AlreadyLoginClientTable {

    private final List<AlreadyLoginClient> list = new ArrayList<>();

    /**
     * <p>put.</p>
     *
     * @param alreadyLoginClient a {@link cool.scx.ext.organization.auth.AlreadyLoginClient} object
     */
    public void add(AlreadyLoginClient... alreadyLoginClient) {
        Collections.addAll(list, alreadyLoginClient);
    }

    /**
     * <p>getByUserID.</p>
     *
     * @param userID a {@link java.lang.Long} object
     * @return an array of {@link cool.scx.ext.organization.auth.AlreadyLoginClient} objects
     */
    public List<AlreadyLoginClient> getByUserID(Long userID) {
        return list.stream().filter(c -> c.userID.equals(userID)).toList();
    }

    /**
     * <p>getByLoginDevice.</p>
     *
     * @param deviceType a {@link cool.scx.ext.organization.auth.DeviceType} object
     * @return an array of {@link cool.scx.ext.organization.auth.AlreadyLoginClient} objects
     */
    public List<AlreadyLoginClient> getByLoginDevice(DeviceType deviceType) {
        return list.stream().filter(c -> c.loginDevice == deviceType).toList();
    }

    /**
     * <p>getByToken.</p>
     *
     * @param token a {@link java.lang.String} object
     * @return a {@link cool.scx.ext.organization.auth.AlreadyLoginClient} object
     */
    public AlreadyLoginClient getByToken(String token) {
        return list.stream().filter(c -> c.token.equals(token)).findAny().orElse(null);
    }

    /**
     * <p>getByWebSocketID.</p>
     *
     * @param webSocketID a {@link java.lang.String} object
     * @return a {@link cool.scx.ext.organization.auth.AlreadyLoginClient} object
     */
    public AlreadyLoginClient getByWebSocketID(String webSocketID) {
        return list.stream().filter(c -> c.webSocketID.equals(webSocketID)).findAny().orElse(null);
    }

    /**
     * <p>getByWebSocketBinaryHandlerID.</p>
     *
     * @param socket a {@link java.lang.String} object
     * @return a {@link cool.scx.ext.organization.auth.AlreadyLoginClient} object
     */
    public AlreadyLoginClient getByWebSocket(ServerWebSocket socket) {
        return getByWebSocketID(socket.binaryHandlerID());
    }

    /**
     * <p>removeByUserID.</p>
     *
     * @param userID a {@link java.lang.Long} object
     * @return a boolean
     */
    public boolean removeByUserID(Long userID) {
        return list.removeIf(c -> c.userID.equals(userID));
    }

    /**
     * <p>removeByLoginDevice.</p>
     *
     * @param deviceType a {@link cool.scx.ext.organization.auth.DeviceType} object
     * @return a boolean
     */
    public boolean removeByLoginDevice(DeviceType deviceType) {
        return list.removeIf(c -> c.loginDevice == deviceType);
    }

    /**
     * <p>removeByToken.</p>
     *
     * @param token a {@link java.lang.String} object
     * @return a boolean
     */
    public boolean removeByToken(String token) {
        return list.removeIf(c -> c.token.equals(token));
    }

    /**
     * <p>removeByWebSocketBinaryHandlerID.</p>
     *
     * @param webSocketID a {@link java.lang.String} object
     * @return a boolean
     */
    public boolean removeByWebSocketID(String webSocketID) {
        return list.removeIf(c -> c.webSocketID.equals(webSocketID));
    }

    /**
     * <p>getAllAlreadyLoginClients.</p>
     *
     * @return an array of {@link cool.scx.ext.organization.auth.AlreadyLoginClient} objects
     */
    public List<AlreadyLoginClient> alreadyLoginClients() {
        return new ArrayList<>(list);
    }

}
