package cool.scx.ext.core;

import cool.scx.vo.VoHelper;

/**
 * 前台和后台发送 websocket 消息的 封装体
 *
 * @author scx567888
 * @version 1.2.2
 */
public record WSBody(String name, Object data) {

    /**
     * a
     *
     * @return a
     */
    public String toJson() {
        return VoHelper.toJson(this, "");
    }

}
