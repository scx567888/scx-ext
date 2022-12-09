package cool.scx.ext.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * <p>QRCodeUtils class.</p>
 *
 * @author scx567888
 * @version 1.3.9
 */
public final class QRCodeUtils {

    /**
     * <p>getQRCode.</p>
     *
     * @param content        二维码内容
     * @param widthAndHeight 图片的宽度和高度 (这里我们规定图片的高度和宽度相同 及图片是个正方形)
     * @return r
     */
    public static byte[] getQRCode(String content, int widthAndHeight) {
        var hints = new HashMap<EncodeHintType, Object>();// 进行编码的一些参数
        hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8); // 指定字符编为 "UTF-8"
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M); // 容错率设为 M (中)
        hints.put(EncodeHintType.MARGIN, 2); // 图片内边距 这里都设置为 2
        try (var b = new ByteArrayOutputStream()) {
            var encode = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight, hints);
            //图片的格式 这里选用一种 兼容性较好且压缩率比较高的 这里用 png
            MatrixToImageWriter.writeToStream(encode, "png", b);
            return b.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
