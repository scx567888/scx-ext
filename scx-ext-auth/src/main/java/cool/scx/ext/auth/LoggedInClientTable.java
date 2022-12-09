package cool.scx.ext.auth;

import io.vertx.core.http.ServerWebSocket;

import java.util.*;

/**
 * <p>AlreadyLoginClientMap class.</p>
 *
 * @author scx567888
 * @version 1.11.8
 */
public final class LoggedInClientTable {

    private final List<LoggedInClient> list = new ArrayList<>();

    /**
     * <p>put.</p>
     *
     * @param loggedInClients a {@link cool.scx.ext.auth.LoggedInClient} object
     */
    public void add(LoggedInClient... loggedInClients) {
        Collections.addAll(list, loggedInClients);
    }

    /**
     * a
     *
     * @param loggedInClients a
     */
    public void addAll(Collection<LoggedInClient> loggedInClients) {
        list.addAll(loggedInClients);
    }

    /**
     * <p>getByUserID.</p>
     *
     * @param userID a {@link java.lang.Long} object
     * @return an array of {@link cool.scx.ext.auth.LoggedInClient} objects
     */
    public List<LoggedInClient> getByUserID(Long userID) {
        return list.stream().filter(c -> Objects.equals(c.userID, userID)).toList();
    }

    /**
     * <p>getByLoginDevice.</p>
     *
     * @param deviceType a {@link cool.scx.ext.auth.DeviceType} object
     * @return an array of {@link cool.scx.ext.auth.LoggedInClient} objects
     */
    public List<LoggedInClient> getByLoginDevice(DeviceType deviceType) {
        return list.stream().filter(c -> Objects.equals(c.loginDevice, deviceType)).toList();
    }

    /**
     * <p>getByToken.</p>
     *
     * @param token a {@link java.lang.String} object
     * @return a {@link cool.scx.ext.auth.LoggedInClient} object
     */
    public LoggedInClient getByToken(String token) {
        return list.stream().filter(c -> Objects.equals(c.token, token)).findAny().orElse(null);
    }

    /**
     * <p>getByWebSocketID.</p>
     *
     * @param webSocketID a {@link java.lang.String} object
     * @return a {@link cool.scx.ext.auth.LoggedInClient} object
     */
    public LoggedInClient getByWebSocketID(String webSocketID) {
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
     * @param deviceType a {@link cool.scx.ext.auth.DeviceType} object
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
     * @return a {@link cool.scx.ext.auth.LoggedInClient} object
     */
    public LoggedInClient getByWebSocket(ServerWebSocket socket) {
        return getByWebSocketID(socket.binaryHandlerID());
    }

    /**
     * <p>getByWebSocketBinaryHandlerID.</p>
     *
     * @param socket a {@link java.lang.String} object
     * @return a {@link cool.scx.ext.auth.LoggedInClient} object
     */
    public boolean removeByWebSocket(ServerWebSocket socket) {
        return removeByWebSocketID(socket.binaryHandlerID());
    }

    /**
     * <p>getAllAlreadyLoginClients.</p>
     *
     * @return an array of {@link cool.scx.ext.auth.LoggedInClient} objects
     */
    public List<LoggedInClient> loggedInClients() {
        return new ArrayList<>(list);
    }

}
