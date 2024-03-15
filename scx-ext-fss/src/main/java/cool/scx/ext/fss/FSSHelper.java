package cool.scx.ext.fss;

import cool.scx.mvc.exception.NotFoundException;
import cool.scx.common.util.FileUtils;
import cool.scx.common.util.RandomUtils;
import net.coobird.thumbnailator.geometry.Positions;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.StandardOpenOption.*;

public final class FSSHelper {
    /**
     * type 和裁剪类型 映射表
     */
    private static final Map<String, Positions> TYPE_POSITIONS_MAP = new HashMap<>();

    static {
        TYPE_POSITIONS_MAP.put("top-left", Positions.TOP_LEFT);
        TYPE_POSITIONS_MAP.put("top-center", Positions.TOP_CENTER);
        TYPE_POSITIONS_MAP.put("top-right", Positions.TOP_RIGHT);
        TYPE_POSITIONS_MAP.put("center-left", Positions.CENTER_LEFT);
        TYPE_POSITIONS_MAP.put("center", Positions.CENTER);
        TYPE_POSITIONS_MAP.put("center-center", Positions.CENTER);
        TYPE_POSITIONS_MAP.put("center-right", Positions.CENTER_RIGHT);
        TYPE_POSITIONS_MAP.put("bottom-left", Positions.BOTTOM_LEFT);
        TYPE_POSITIONS_MAP.put("bottom-center", Positions.BOTTOM_CENTER);
        TYPE_POSITIONS_MAP.put("bottom-right", Positions.BOTTOM_RIGHT);
        //简写
        TYPE_POSITIONS_MAP.put("tl", Positions.TOP_LEFT);
        TYPE_POSITIONS_MAP.put("tc", Positions.TOP_CENTER);
        TYPE_POSITIONS_MAP.put("tr", Positions.TOP_RIGHT);
        TYPE_POSITIONS_MAP.put("cl", Positions.CENTER_LEFT);
        TYPE_POSITIONS_MAP.put("c", Positions.CENTER);
        TYPE_POSITIONS_MAP.put("cc", Positions.CENTER);
        TYPE_POSITIONS_MAP.put("cr", Positions.CENTER_RIGHT);
        TYPE_POSITIONS_MAP.put("bl", Positions.BOTTOM_LEFT);
        TYPE_POSITIONS_MAP.put("bc", Positions.BOTTOM_CENTER);
        TYPE_POSITIONS_MAP.put("br", Positions.BOTTOM_RIGHT);
    }

    public static Positions getPositions(String type) {
        if (type == null) {
            return null;
        }
        return TYPE_POSITIONS_MAP.get(type.toLowerCase());
    }

    /**
     * 获取物理文件路径
     *
     * @param fssObject a {@link cool.scx.ext.fss.FSSObject} object
     * @return a {@link java.nio.file.Path} object
     */
    public static Path getPhysicalFilePath(FSSObject fssObject) {
        return Path.of(FSSConfig.uploadFilePath().toString(), fssObject.filePath);
    }

    /**
     * 检查物理文件是否存在 存在则返回物理文件 不存在则抛出异常
     *
     * @param fssObject a {@link cool.scx.ext.fss.FSSObject} object
     * @return a {@link java.io.File} object
     * @throws cool.scx.mvc.exception.NotFoundException if any.
     */
    public static Path checkPhysicalFile(FSSObject fssObject) throws NotFoundException {
        var physicalFile = getPhysicalFilePath(fssObject);
        if (Files.notExists(physicalFile)) {
            throw new NotFoundException();
        }
        return physicalFile;
    }

    public static Path getUploadTempPath(String fileHash) {
        return FSSConfig.uploadFilePath().resolve("TEMP").resolve(fileHash);
    }

    public static FSSObject copyFSSObject(String fileName, FSSObject oldFSSObject) {
        var fssObject = new FSSObject();
        fssObject.fssObjectID = RandomUtils.randomUUID();
        fssObject.fileName = fileName;
        fssObject.uploadTime = LocalDateTime.now();
        fssObject.filePath = oldFSSObject.filePath;
        fssObject.fileSizeDisplay = oldFSSObject.fileSizeDisplay;
        fssObject.fileSize = oldFSSObject.fileSize;
        fssObject.fileHash = oldFSSObject.fileHash;
        fssObject.fileExtension = FileUtils.getExtension(fssObject.fileName);
        return fssObject;
    }


    /**
     * 根据文件信息 创建 FSSObject 实例
     * 规则如下
     * fssObjectID (文件 id)        : 随机字符串
     * filePath (文件物理文件存储路径) : 年份(以上传时间为标准)/月份(以上传时间为标准)/天(以上传时间为标准)/文件MD5/文件真实名称
     * 其他字段和字面意义相同
     *
     * @param fileName a {@link java.lang.String} object.
     * @param fileSize a {@link java.lang.Long} object.
     * @param fileHash a {@link java.lang.String} object.
     * @return a {@link cool.scx.ext.fss.FSSObject} object.
     */
    public static FSSObject createFSSObjectByFileInfo(String fileName, Long fileSize, String fileHash) {
        var now = LocalDateTime.now();
        var yearStr = String.valueOf(now.getYear());
        var monthStr = String.valueOf(now.getMonthValue());
        var dayStr = String.valueOf(now.getDayOfMonth());
        var fssObject = new FSSObject();
        fssObject.fssObjectID = RandomUtils.randomUUID();
        fssObject.fileName = fileName;
        fssObject.uploadTime = now;
        fssObject.fileSizeDisplay = FileUtils.longToDisplaySize(fileSize);
        fssObject.fileSize = fileSize;
        fssObject.fileHash = fileHash;
        fssObject.fileExtension = FileUtils.getExtension(fssObject.fileName);
        fssObject.filePath = new String[]{yearStr, monthStr, dayStr, fileHash, fileName};
        return fssObject;
    }


    /**
     * <p>getLastUploadChunk.</p>
     *
     * @param uploadConfigFile a {@link java.io.File} object.
     * @param chunkLength      a {@link java.lang.Integer} object.
     * @return a {@link java.lang.Integer} object.
     * @throws java.io.IOException e
     */
    public static Integer getLastUploadChunk(Path uploadConfigFile, Integer chunkLength) throws IOException {
        try {
            var allStr = Files.readString(uploadConfigFile);
            return Integer.parseInt(allStr.split("_")[0]);
        } catch (Exception e) {
            //-1 表示文件从未上传过
            updateLastUploadChunk(uploadConfigFile, -1, chunkLength);
            return -1;
        }
    }

    /**
     * 更新最后一次文件上传的区块
     *
     * @param uploadConfigFile a {@link java.io.File} object.
     * @param nowChunkIndex    a {@link java.lang.Integer} object.
     * @param chunkLength      a {@link java.lang.Integer} object.
     * @throws java.io.IOException e
     */
    public static void updateLastUploadChunk(Path uploadConfigFile, Integer nowChunkIndex, Integer chunkLength) throws IOException {
        var str = nowChunkIndex + "_" + chunkLength;
        FileUtils.write(uploadConfigFile, str.getBytes(StandardCharsets.UTF_8), TRUNCATE_EXISTING, CREATE, SYNC, WRITE);
    }

}
