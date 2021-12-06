package cool.scx.ext.message;

import cool.scx.ScxContext;
import cool.scx.annotation.ScxService;
import cool.scx.util.Base64Utils;
import cool.scx.util.HttpUtils;
import cool.scx.util.digest.DigestUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 云通讯短信发送功能
 *
 * @author scx567888
 * @version 1.1.9
 */
@ScxService
public class YTXTextMessageSender {

    private static final String YTX_BASE_URL = "https://app.cloopen.com:8883";
    private final String YTX_ACCOUNT_SID;
    private final String YTX_AUTH_TOKEN;
    private final String YTX_APP_ID;

    private final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /**
     * <p>Constructor for YTXTextMessageSender.</p>
     */
    public YTXTextMessageSender() {
        YTX_ACCOUNT_SID = ScxContext.config().get("message.ytx-account-sid", String.class);
        YTX_AUTH_TOKEN = ScxContext.config().get("message.ytx-auth-token", String.class);
        YTX_APP_ID = ScxContext.config().get("message.ytx-app-id", String.class);
    }

    /**
     * {@inheritDoc}
     * <p>
     * 向手机号发送短信
     *
     * @param address a {@link java.util.List} object
     * @param message a {@link java.util.Map} object
     * @return a {@link java.lang.String} object
     */
    public String send(List<String> address, Map<String, Object> message) throws IOException, InterruptedException {
        var timeStampStr = getTimeStampStr();
        var authorization = getAuthorization(timeStampStr);
        var sigParameter = getSigParameter(timeStampStr);

        var map = new HashMap<String, Object>();
        map.put("to", address.stream().collect(Collectors.joining(",", "", "")));
        map.put("appId", YTX_APP_ID);
        map.put("templateId", message.get("templateId"));
        map.put("datas", message.get("datas"));

        var header = new HashMap<String, String>();
        header.put("Authorization", authorization);
        var post = HttpUtils.post(getSendUrl(sigParameter), header, map);
        return post.body();
    }

    private String getSendUrl(String sigParameter) {
        var s = "/2013-12-26/Accounts/" + YTX_ACCOUNT_SID + "/SMS/TemplateSMS?sig=" + sigParameter;
        return YTX_BASE_URL + s;
    }

    private String getSigParameter(String TimeStampStr) {
        return DigestUtils.md5(YTX_ACCOUNT_SID + YTX_AUTH_TOKEN + TimeStampStr);
    }

    private String getAuthorization(String TimeStampStr) {
        return Base64Utils.encode(YTX_ACCOUNT_SID + ":" + TimeStampStr);
    }

    private String getTimeStampStr() {
        return LocalDateTime.now().format(DATETIME_FORMAT);
    }
}
