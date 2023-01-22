package cool.scx.ext.fss;

import cool.scx.core.annotation.*;
import cool.scx.enumeration.HttpMethod;
import cool.scx.core.http.exception.InternalServerErrorException;
import cool.scx.core.http.exception.NotFoundException;
import cool.scx.core.type.UploadedEntity;
import cool.scx.core.vo.*;
import cool.scx.util.Cache;
import cool.scx.util.DigestUtils;
import cool.scx.util.FileUtils;
import cool.scx.util.RandomUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static cool.scx.ext.fss.FSSObjectService.getPhysicalFilePath;
import static java.nio.file.StandardOpenOption.*;

/**
 * <p>FSSController class.</p>
 *
 * @author scx567888
 * @version 0.3.6
 */
@ScxMapping("/api/fss")
public class FSSController {

    /**
     * 图片缓存 此处做一些初始设置
     * 设置缓存的最大容量 为 10000 .
     */
    private static final Cache<String, Image> IMAGE_CACHE = new Cache<>(10000);

    /**
     * a
     */
    private final FSSObjectService fssObjectService;

    /**
     * 构造函数
     *
     * @param fssObjectService a {@link cool.scx.ext.fss.FSSObjectService} object
     */
    public FSSController(FSSObjectService fssObjectService) {
        this.fssObjectService = fssObjectService;
    }

    /**
     * 检查物理文件是否存在 存在则返回物理文件 不存在则抛出异常
     *
     * @param fssObject a {@link cool.scx.ext.fss.FSSObject} object
     * @return a {@link java.io.File} object
     * @throws cool.scx.core.http.exception.NotFoundException if any.
     */
    private static Path checkPhysicalFile(FSSObject fssObject) throws NotFoundException {
        var physicalFile = getPhysicalFilePath(fssObject);
        if (Files.notExists(physicalFile)) {
            throw new NotFoundException();
        }
        return physicalFile;
    }

    /**
     * a
     *
     * @param fileMD5 a
     * @return a
     */
    private static Path getUploadTempPath(String fileMD5) {
        return FSSConfig.uploadFilePath().resolve("TEMP").resolve(fileMD5);
    }

    /**
     * <p>copyUploadFile.</p>
     *
     * @param fileName     a {@link java.lang.String} object.
     * @param oldFSSObject a {@link cool.scx.ext.fss.FSSObject} object.
     * @return a {@link cool.scx.ext.fss.FSSObject} object.
     */
    public static FSSObject copyFSSObject(String fileName, FSSObject oldFSSObject) {
        var fssObject = new FSSObject();
        fssObject.fssObjectID = RandomUtils.randomUUID();
        fssObject.fileName = fileName;
        fssObject.uploadTime = LocalDateTime.now();
        fssObject.filePath = oldFSSObject.filePath;
        fssObject.fileSizeDisplay = oldFSSObject.fileSizeDisplay;
        fssObject.fileSize = oldFSSObject.fileSize;
        fssObject.fileMD5 = oldFSSObject.fileMD5;
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
     * @param fileMD5  a {@link java.lang.String} object.
     * @return a {@link cool.scx.ext.fss.FSSObject} object.
     */
    public static FSSObject createFSSObjectByFileInfo(String fileName, Long fileSize, String fileMD5) {
        var now = LocalDateTime.now();
        var yearStr = now.getYear() + "";
        var monthStr = now.getMonthValue() + "";
        var dayStr = now.getDayOfMonth() + "";
        var fssObject = new FSSObject();
        fssObject.fssObjectID = RandomUtils.randomUUID();
        fssObject.fileName = fileName;
        fssObject.uploadTime = now;
        fssObject.fileSizeDisplay = FileUtils.longToDisplaySize(fileSize);
        fssObject.fileSize = fileSize;
        fssObject.fileMD5 = fileMD5;
        fssObject.fileExtension = FileUtils.getExtension(fssObject.fileName);
        fssObject.filePath = new String[]{yearStr, monthStr, dayStr, fileMD5, fileName};
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

    /**
     * 通用下载资源方法
     * todo 优化性能
     *
     * @param fssObjectID a {@link java.lang.String} object.
     * @return a {@link cool.scx.core.vo.Download} object.
     */
    @ScxMapping(value = "/download/:fssObjectID", method = {HttpMethod.GET, HttpMethod.HEAD})
    public Download download(@FromPath String fssObjectID) {
        var fssObject = checkFSSObjectID(fssObjectID);
        var file = checkPhysicalFile(fssObject);
        return Download.of(file, fssObject.fileName);
    }

    /**
     * 展示图片
     *
     * @param fssObjectID id
     * @param width       a {@link java.lang.Integer} object.
     * @param height      a {@link java.lang.Integer} object.
     * @param type        a {@link java.lang.String} object
     * @return a {@link cool.scx.core.vo.Raw} object.
     */
    @ScxMapping(value = "/image/:fssObjectID", method = {HttpMethod.GET, HttpMethod.HEAD})
    public Image image(@FromPath String fssObjectID,
                       @FromQuery(value = "w", required = false) Integer width,
                       @FromQuery(value = "h", required = false) Integer height,
                       @FromQuery(value = "t", required = false) String type) {
        var cacheKey = fssObjectID + " " + width + " " + height + " " + type;
        //尝试通过缓存获取
        return IMAGE_CACHE.computeIfAbsent(cacheKey, k -> {
            var fssObject = checkFSSObjectID(fssObjectID);
            var file = checkPhysicalFile(fssObject);
            return Image.of(file.toFile(), width, height, type);
        });
    }

    /**
     * 展示文件
     *
     * @param fssObjectID id
     * @return a {@link cool.scx.core.vo.Raw} object.
     */
    @ScxMapping(value = "/raw/:fssObjectID", method = {HttpMethod.GET, HttpMethod.HEAD})
    public Raw raw(@FromPath String fssObjectID) {
        var fssObject = checkFSSObjectID(fssObjectID);
        var file = checkPhysicalFile(fssObject);
        return Raw.of(file);
    }

    /**
     * 单个文件上传 和 分片文件上传
     *
     * @param fileName      文件名
     * @param fileSize      文件大小
     * @param fileMD5       文件md5
     * @param chunkLength   分片总长度
     * @param nowChunkIndex 当前分片
     * @param fileData      文件内容
     * @return r
     * @throws java.lang.Exception s
     */
    @ScxMapping(value = "/upload", method = HttpMethod.POST)
    public Json upload(@FromBody String fileName,
                       @FromBody Long fileSize,
                       @FromBody String fileMD5,
                       @FromBody Integer chunkLength,
                       @FromBody Integer nowChunkIndex,
                       @FromUpload UploadedEntity fileData) throws Exception {
        var uploadTempFile = getUploadTempPath(fileMD5).resolve("scx_fss.temp");
        var uploadConfigFile = uploadTempFile.resolveSibling("scx_fss.upload_state");

        //判断是否上传的是最后一个分块 (因为 索引是以 0 开头的所以这里 -1)
        if (nowChunkIndex == chunkLength - 1) {
            //先将数据写入临时文件中
            FileUtils.write(uploadTempFile, fileData.buffer().getBytes(), APPEND, CREATE, SYNC, WRITE);
            //获取文件描述信息创建 fssObject 对象
            var newFSSObject = createFSSObjectByFileInfo(fileName, fileSize, fileMD5);
            //获取文件真实的存储路径
            var fileStoragePath = Path.of(FSSConfig.uploadFilePath().toString(), newFSSObject.filePath);
            //计算 md5 只有前后台 md5 相同文件才算 正确
            var serverMd5Str = DigestUtils.md5(uploadTempFile.toFile());
            if (!fileMD5.equalsIgnoreCase(serverMd5Str)) {
                //md5 不相同 说明临时文件可能损坏 删除临时文件
                FileUtils.delete(uploadTempFile.getParent());
                throw new InternalServerErrorException("上传文件失败 : MD5 校验失败 , 文件 : " + fileMD5);
            }
            //移动成功 说明文件上传成功
            //将临时文件移动并重命名到 真实的存储路径
            FileUtils.move(uploadTempFile, fileStoragePath);
            //尝试设置为只读
            fileStoragePath.toFile().setReadOnly();
            //删除临时文件夹
            FileUtils.delete(uploadTempFile.getParent());
            //存储到数据库
            var save = fssObjectService.add(newFSSObject);
            //像前台发送上传成功的消息
            return Json.ok().put("type", "upload-success").put("item", save);
        } else {
            //这里我们从文件中读取上次(最后一次)上传到了哪个区块
            var lastUploadChunk = getLastUploadChunk(uploadConfigFile, chunkLength);
            //需要的区块索引 这里就是 当前最后一次的区块 + 1 因为上一次已经上传完了 我们需要的是下一块
            var needUploadChunkIndex = lastUploadChunk + 1;
            //当前的区块索引和需要的区块索引相同 就保存文件内容
            if (nowChunkIndex.equals(needUploadChunkIndex)) {
                FileUtils.write(uploadTempFile, fileData.buffer().getBytes(), APPEND, CREATE, SYNC, WRITE);
                //将当前上传成功的区块索引和总区块长度保存到配置文件中
                updateLastUploadChunk(uploadConfigFile, nowChunkIndex, chunkLength);
                //像前台返回我们需要的下一个区块索引
                return Json.ok().put("type", "need-more").put("item", needUploadChunkIndex + 1);
            } else {//否则的话 我们向前台返回我们需要的区块索引
                return Json.ok().put("type", "need-more").put("item", needUploadChunkIndex);
            }
        }
    }

    /**
     * a
     *
     * @param fssObjectID a
     * @return a
     * @throws java.io.IOException a
     */
    @ScxMapping(value = "/delete", method = HttpMethod.DELETE)
    public Json delete(@FromBody String fssObjectID) throws IOException {
        //先获取文件的基本信息
        fssObjectService.delete(fssObjectID);
        return Json.ok();
    }

    /**
     * 检查一下这个 服务器里有没有和这个 可以直接使用 此 md5 的文件
     *
     * @param fileName f
     * @param fileSize f
     * @param fileMD5  f
     * @return f
     * @throws java.io.IOException f
     */
    @ScxMapping(value = "check-any-file-exists-by-this-md5", method = HttpMethod.POST)
    public Json checkAnyFileExistsByThisMD5(@FromBody String fileName,
                                            @FromBody Long fileSize,
                                            @FromBody String fileMD5) throws IOException {
        //先判断 文件是否已经上传过 并且文件可用
        var fssObjectListByMd5 = fssObjectService.findFSSObjectListByMD5(fileMD5);
        if (fssObjectListByMd5.size() > 0) {
            FSSObject canUseFssObject = null;//先假设一个可以使用的文件
            //循环处理
            for (var fssObject : fssObjectListByMd5) {
                //获取物理文件
                var physicalFile = getPhysicalFilePath(fssObject).toFile();
                //这里多校验一些内容避免出先差错
                //第一 文件必须存在 第二 文件大小必须和前台获得的文件大小相同 第三 文件的 md5 校验结果也必须和前台发送过来的 md5 相同
                if (physicalFile.exists() && physicalFile.length() == fileSize && fileMD5.equalsIgnoreCase(DigestUtils.md5(physicalFile))) {
                    //这些都通过表示文件是可用的 赋值并跳出循环
                    canUseFssObject = fssObject;
                    break;
                }
            }
            //起码找到了一个 可以使用的文件
            if (canUseFssObject != null) {
                var save = fssObjectService.add(copyFSSObject(fileName, canUseFssObject));
                //有可能有之前的残留临时文件 再此一并清除
                FileUtils.delete(getUploadTempPath(fileMD5));
                //通知前台秒传成功
                return Json.ok().put("type", "upload-by-md5-success").put("item", save);
            }
        }
        //通知前台 没找到任何 和此 MD5 相同并且文件内容未损害的 文件
        return Json.fail("no-any-file-exists-for-this-md5");
    }

    /**
     * <p>listFile.</p>
     *
     * @param fssObjectID a {@link java.util.Map} object.
     * @return a {@link cool.scx.core.vo.Json} object.
     */
    @ScxMapping(value = "/info", method = HttpMethod.POST)
    public BaseVo info(@FromBody String fssObjectID) {
        if (fssObjectID != null) {
            return DataJson.ok().data(fssObjectService.findByFSSObjectID(fssObjectID));
        } else {
            return DataJson.ok().data(null);
        }
    }

    /**
     * s
     *
     * @param fssObjectIDs a
     * @return a
     */
    @ScxMapping(value = "/list-info", method = HttpMethod.POST)
    public BaseVo listInfo(@FromBody List<String> fssObjectIDs) {
        if (fssObjectIDs != null && fssObjectIDs.size() > 0) {
            return DataJson.ok().data(fssObjectService.findByFSSObjectIDs(fssObjectIDs));
        } else {
            return DataJson.ok().data(new ArrayList<>());
        }
    }

    /**
     * <p>checkFileID.</p>
     *
     * @param fssObjectID a {@link java.lang.String} object
     * @return a {@link cool.scx.ext.fss.FSSObject} object
     */
    private FSSObject checkFSSObjectID(String fssObjectID) {
        var fssObject = fssObjectService.findByFSSObjectID(fssObjectID);
        if (fssObject == null) {
            throw new NotFoundException();
        }
        return fssObject;
    }

}
