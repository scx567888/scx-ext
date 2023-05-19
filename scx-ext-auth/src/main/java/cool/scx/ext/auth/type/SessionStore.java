package cool.scx.ext.auth.type;

import io.vertx.core.http.ServerWebSocket;

import java.util.*;

/**
 * <p>AlreadyLoginClientMap class.</p>
 *
 * @author scx567888
 * @version 1.11.8
 */
public final class SessionStore {

    private final List<Session> list = new ArrayList<>();

    /**
     * <p>put.</p>
     *
     * @param loggedInClients a {@link Session} object
     */
    public void add(Session... loggedInClients) {
        Collections.addAll(list, loggedInClients);
    }

    /**
     * a
     *
     * @param loggedInClients a
     */
    public void addAll(Collection<Session> loggedInClients) {
        list.addAll(loggedInClients);
    }

    /**
     * <p>getByUserID.</p>
     *
     * @param userID a {@link java.lang.Long} object
     * @return an  of {@link Session} objects
     */
    public List<Session> getByUserID(Long userID) {
        return list.stream().filter(c -> Objects.equals(c.userID(), userID)).toList();
    }

    /**
     * <p>getByLoginDevice.</p>
     *
     * @param deviceType a {@link DeviceType} object
     * @return an of {@link Session} objects
     */
    public List<Session> getByLoginDevice(DeviceType deviceType) {
        return list.stream().filter(c -> Objects.equals(c.loginDevice(), deviceType)).toList();
    }

    /**
     * <p>getByToken.</p>
     *
     * @param token a {@link java.lang.String} object
     * @return a {@link Session} object
     */
    public Session getByToken(String token) {
        return list.stream().filter(c -> Objects.equals(c.token(), token)).findAny().orElse(null);
    }

    /**
     * <p>removeByUserID.</p>
     *
     * @param userID a {@link java.lang.Long} object
     * @return a boolean
     */
    public boolean removeByUserID(Long userID) {
        return list.removeIf(c -> Objects.equals(c.userID(), userID));
    }

    /**
     * <p>removeByLoginDevice.</p>
     *
     * @param deviceType a {@link DeviceType} object
     * @return a boolean
     */
    public boolean removeByLoginDevice(DeviceType deviceType) {
        return list.removeIf(c -> Objects.equals(c.loginDevice(), deviceType));
    }

    /**
     * <p>removeByToken.</p>
     *
     * @param token a {@link java.lang.String} object
     * @return a boolean
     */
    public boolean removeByToken(String token) {
        return list.removeIf(c -> Objects.equals(c.token(), token));
    }

    /**
     * getByWebSocket
     *
     * @param webSocket a {@link java.lang.String} object
     * @return a {@link Session} object
     */
    public Session getByWebSocket(ServerWebSocket webSocket) {
        return list.stream().filter(c -> Objects.equals(c.webSocket, webSocket)).findAny().orElse(null);
    }

    /**
     * removeByWebSocket
     *
     * @param webSocket a {@link java.lang.String} object
     * @return a {@link Session} object
     */
    public boolean removeByWebSocket(ServerWebSocket webSocket) {
        return list.removeIf(c -> Objects.equals(c.webSocket, webSocket));
    }

    /**
     * <p>getAllAlreadyLoginClients.</p>
     *
     * @return an of {@link Session} objects
     */
    public List<Session> loggedInClients() {
        return new ArrayList<>(list);
    }

}
