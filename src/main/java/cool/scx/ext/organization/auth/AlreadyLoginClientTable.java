package cool.scx.ext.organization.auth;

import io.vertx.core.http.ServerWebSocket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
        return list.stream().filter(c -> Objects.equals(c.userID, userID)).toList();
    }

    /**
     * <p>getByLoginDevice.</p>
     *
     * @param deviceType a {@link cool.scx.ext.organization.auth.DeviceType} object
     * @return an array of {@link cool.scx.ext.organization.auth.AlreadyLoginClient} objects
     */
    public List<AlreadyLoginClient> getByLoginDevice(DeviceType deviceType) {
        return list.stream().filter(c -> Objects.equals(c.loginDevice, deviceType)).toList();
    }

    /**
     * <p>getByToken.</p>
     *
     * @param token a {@link java.lang.String} object
     * @return a {@link cool.scx.ext.organization.auth.AlreadyLoginClient} object
     */
    public AlreadyLoginClient getByToken(String token) {
        return list.stream().filter(c -> Objects.equals(c.token, token)).findAny().orElse(null);
    }

    /**
     * <p>getByWebSocketID.</p>
     *
     * @param webSocketID a {@link java.lang.String} object
     * @return a {@link cool.scx.ext.organization.auth.AlreadyLoginClient} object
     */
    public AlreadyLoginClient getByWebSocketID(String webSocketID) {
        return list.stream().filter(c -> Objects.equals(c.webSocketID, webSocketID)).findAny().orElse(null);
    }

    /**
     * <p>removeByUserID.</p>
     *
     * @param userID a {@link java.lang.Long} object
     * @return a boolean
     */
    public boolean removeByUserID(Long userID) {
        return list.removeIf(c -> Objects.equals(c.userID, userID));
    }

    /**
     * <p>removeByLoginDevice.</p>
     *
     * @param deviceType a {@link cool.scx.ext.organization.auth.DeviceType} object
     * @return a boolean
     */
    public boolean removeByLoginDevice(DeviceType deviceType) {
        return list.removeIf(c -> Objects.equals(c.loginDevice, deviceType));
    }

    /**
     * <p>removeByToken.</p>
     *
     * @param token a {@link java.lang.String} object
     * @return a boolean
     */
    public boolean removeByToken(String token) {
        return list.removeIf(c -> Objects.equals(c.token, token));
    }

    /**
     * <p>removeByWebSocketBinaryHandlerID.</p>
     *
     * @param webSocketID a {@link java.lang.String} object
     * @return a boolean
     */
    public boolean removeByWebSocketID(String webSocketID) {
        return list.removeIf(c -> Objects.equals(c.webSocketID, webSocketID));
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
     * <p>getByWebSocketBinaryHandlerID.</p>
     *
     * @param socket a {@link java.lang.String} object
     * @return a {@link cool.scx.ext.organization.auth.AlreadyLoginClient} object
     */
    public boolean removeByWebSocket(ServerWebSocket socket) {
        return removeByWebSocketID(socket.binaryHandlerID());
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
