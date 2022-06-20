package cool.scx.ext.organization.auth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>AlreadyLoginClientMap class.</p>
 *
 * @author scx567888
 * @version 1.11.8
 */
public final class AlreadyLoginClientMap {

    private final List<AlreadyLoginClient> list = new ArrayList<>();

    /**
     * <p>put.</p>
     *
     * @param alreadyLoginClient a {@link cool.scx.ext.organization.auth.AlreadyLoginClient} object
     */
    public void put(AlreadyLoginClient... alreadyLoginClient) {
        Collections.addAll(list, alreadyLoginClient);
    }

    /**
     * <p>getByUserID.</p>
     *
     * @param userID a {@link java.lang.Long} object
     * @return an array of {@link cool.scx.ext.organization.auth.AlreadyLoginClient} objects
     */
    public AlreadyLoginClient[] getByUserID(Long userID) {
        return list.stream().filter(c -> c.userID.equals(userID)).toArray(AlreadyLoginClient[]::new);
    }

    /**
     * <p>getByLoginDevice.</p>
     *
     * @param deviceType a {@link cool.scx.ext.organization.auth.DeviceType} object
     * @return an array of {@link cool.scx.ext.organization.auth.AlreadyLoginClient} objects
     */
    public AlreadyLoginClient[] getByLoginDevice(DeviceType deviceType) {
        return list.stream().filter(c -> c.loginDevice == deviceType).toArray(AlreadyLoginClient[]::new);
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
     * <p>getByWebSocketBinaryHandlerID.</p>
     *
     * @param webSocketBinaryHandlerID a {@link java.lang.String} object
     * @return a {@link cool.scx.ext.organization.auth.AlreadyLoginClient} object
     */
    public AlreadyLoginClient getByWebSocketBinaryHandlerID(String webSocketBinaryHandlerID) {
        return list.stream().filter(c -> c.webSocketBinaryHandlerID.equals(webSocketBinaryHandlerID)).findAny().orElse(null);
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
     * @param webSocketBinaryHandlerID a {@link java.lang.String} object
     * @return a boolean
     */
    public boolean removeByWebSocketBinaryHandlerID(String webSocketBinaryHandlerID) {
        return list.removeIf(c -> c.webSocketBinaryHandlerID.equals(webSocketBinaryHandlerID));
    }

    /**
     * <p>getAllAlreadyLoginClients.</p>
     *
     * @return an array of {@link cool.scx.ext.organization.auth.AlreadyLoginClient} objects
     */
    public AlreadyLoginClient[] getAllAlreadyLoginClients() {
        return list.toArray(AlreadyLoginClient[]::new);
    }

}
