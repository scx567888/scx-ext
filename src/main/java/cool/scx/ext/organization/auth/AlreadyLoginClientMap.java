package cool.scx.ext.organization.auth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class AlreadyLoginClientMap {

    private final List<AlreadyLoginClient> list = new ArrayList<>();

    public void put(AlreadyLoginClient... alreadyLoginClient) {
        Collections.addAll(list, alreadyLoginClient);
    }

    public AlreadyLoginClient[] getByUserID(Long userID) {
        return list.stream().filter(c -> c.userID.equals(userID)).toArray(AlreadyLoginClient[]::new);
    }

    public AlreadyLoginClient[] getByLoginDevice(DeviceType deviceType) {
        return list.stream().filter(c -> c.loginDevice == deviceType).toArray(AlreadyLoginClient[]::new);
    }

    public AlreadyLoginClient getByToken(String token) {
        return list.stream().filter(c -> c.token.equals(token)).findAny().orElse(null);
    }

    public AlreadyLoginClient getByWebSocketBinaryHandlerID(String webSocketBinaryHandlerID) {
        return list.stream().filter(c -> c.webSocketBinaryHandlerID.equals(webSocketBinaryHandlerID)).findAny().orElse(null);
    }

    public boolean removeByUserID(Long userID) {
        return list.removeIf(c -> c.userID.equals(userID));
    }

    public boolean removeByLoginDevice(DeviceType deviceType) {
        return list.removeIf(c -> c.loginDevice == deviceType);
    }

    public boolean removeByToken(String token) {
        return list.removeIf(c -> c.token.equals(token));
    }

    public boolean removeByWebSocketBinaryHandlerID(String webSocketBinaryHandlerID) {
        return list.removeIf(c -> c.webSocketBinaryHandlerID.equals(webSocketBinaryHandlerID));
    }

    public AlreadyLoginClient[] getAllAlreadyLoginClients() {
        return list.toArray(AlreadyLoginClient[]::new);
    }

}
