package cool.scx.ext.fss;

import cool.scx.standard.HttpMethod;
import cool.scx.mvc.annotation.*;
import cool.scx.mvc.exception.InternalServerErrorException;
import cool.scx.mvc.exception.NotFoundException;
import cool.scx.mvc.type.UploadedEntity;
import cool.scx.mvc.vo.*;
import cool.scx.util.Cache;
import cool.scx.util.FileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static cool.scx.ext.fss.FSSHelper.*;
import static cool.scx.util.HashUtils.md5Hex;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardOpenOption.*;

/**
 * <p>FSSController class.</p>
 *
 * @author scx567888
 * @version 0.3.6
 */
@ScxRoute("/api/fss")
public class FSSController {

    /**
     * 图片缓存 此处做一些初始设置
     * 设置缓存的最大容量 为 10000 .
     */
    private static final Cache<String, Image> IMAGE_CACHE = new Cache<>(10000);
    private final FSSObjectService fssObjectService;

    public FSSController(FSSObjectService fssObjectService) {
        this.fssObjectService = fssObjectService;
    }

    @ScxRoute(value = "/download/:fssObjectID", methods = {HttpMethod.GET, HttpMethod.HEAD})
    public Download download(@FromPath String fssObjectID) {
        var fssObject = checkFSSObjectID(fssObjectID);
        var file = checkPhysicalFile(fssObject);
        return Download.of(file, fssObject.fileName);
    }

    @ScxRoute(value = "/image/:fssObjectID", methods = {HttpMethod.GET, HttpMethod.HEAD})
    public Image image(@FromPath String fssObjectID,
                       @FromQuery(value = "w", required = false) Integer width,
                       @FromQuery(value = "h", required = false) Integer height,
                       @FromQuery(value = "t", required = false) String type) {
        // positions 可以为 null
        var positions = FSSHelper.getPositions(type);
        var cacheKey = fssObjectID + " " + width + " " + height + " " + positions;
        //尝试通过缓存获取
        return IMAGE_CACHE.computeIfAbsent(cacheKey, k -> {
            var fssObject = checkFSSObjectID(fssObjectID);
            var file = checkPhysicalFile(fssObject);
            return Image.of(file.toFile(), width, height, positions);
        });
    }

    @ScxRoute(value = "/raw/:fssObjectID", methods = {HttpMethod.GET, HttpMethod.HEAD})
    public Raw raw(@FromPath String fssObjectID) {
        var fssObject = checkFSSObjectID(fssObjectID);
        var file = checkPhysicalFile(fssObject);
        return Raw.of(file);
    }

    @ScxRoute(value = "/upload", methods = HttpMethod.POST)
    public BaseVo upload(
            @FromBody String fileName, //文件名
            @FromBody Long fileSize,//文件大小
            @FromBody String fileHash,// 文件 Hash
            @FromBody Integer chunkLength,//分片总长度
            @FromBody Integer nowChunkIndex,//当前分片
            @FromUpload UploadedEntity fileData//文件内容
    ) throws Exception {
        var uploadTempFile = getUploadTempPath(fileHash).resolve("scx_fss.temp");
        var uploadConfigFile = uploadTempFile.resolveSibling("scx_fss.upload_state");

        //判断是否上传的是最后一个分块 (因为 索引是以 0 开头的所以这里 -1)
        if (nowChunkIndex == chunkLength - 1) {
            //先将数据写入临时文件中
            FileUtils.write(uploadTempFile, fileData.buffer().getBytes(), APPEND, CREATE, SYNC, WRITE);
            //获取文件描述信息创建 fssObject 对象
            var newFSSObject = createFSSObjectByFileInfo(fileName, fileSize, fileHash);
            //获取文件真实的存储路径
            var fileStoragePath = Path.of(FSSConfig.uploadFilePath().toString(), newFSSObject.filePath);
            //计算 md5 只有前后台 md5 相同文件才算 正确
            var serverHashStr = md5Hex(uploadTempFile.toFile());
            if (!fileHash.equalsIgnoreCase(serverHashStr)) {
                //md5 不相同 说明临时文件可能损坏 删除临时文件
                FileUtils.delete(uploadTempFile.getParent());
                throw new InternalServerErrorException("上传文件失败 : Hash 校验失败 , 文件 : " + fileHash);
            }
            //移动成功 说明文件上传成功
            //将临时文件移动并重命名到 真实的存储路径
            FileUtils.move(uploadTempFile, fileStoragePath, REPLACE_EXISTING);
            //尝试设置为只读
            fileStoragePath.toFile().setReadOnly();
            //删除临时文件夹
            FileUtils.delete(uploadTempFile.getParent());
            //存储到数据库
            var save = fssObjectService.add(newFSSObject);
            //像前台发送上传成功的消息
            return Result.ok().put("type", "upload-success").put("item", save);
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
                return Result.ok().put("type", "need-more").put("item", needUploadChunkIndex + 1);
            } else {//否则的话 我们向前台返回我们需要的区块索引
                return Result.ok().put("type", "need-more").put("item", needUploadChunkIndex);
            }
        }
    }

    @ScxRoute(value = "/delete", methods = HttpMethod.DELETE)
    public BaseVo delete(@FromBody String fssObjectID) throws IOException {
        //先获取文件的基本信息
        fssObjectService.delete(fssObjectID);
        return Result.ok();
    }

    @ScxRoute(value = "check-any-file-exists-by-hash", methods = HttpMethod.POST)
    public BaseVo checkAnyFileExistsByHash(@FromBody String fileName,
                                           @FromBody Long fileSize,
                                           @FromBody String fileHash) throws IOException {
        //先判断 文件是否已经上传过 并且文件可用
        var fssObjectListByHash = fssObjectService.findFSSObjectListByHash(fileHash);
        if (fssObjectListByHash.size() > 0) {
            FSSObject canUseFssObject = null;//先假设一个可以使用的文件
            //循环处理
            for (var fssObject : fssObjectListByHash) {
                //获取物理文件
                var physicalFile = getPhysicalFilePath(fssObject).toFile();
                //这里多校验一些内容避免出先差错
                //第一 文件必须存在 第二 文件大小必须和前台获得的文件大小相同 第三 文件的 md5 校验结果也必须和前台发送过来的 md5 相同
                if (physicalFile.exists() && physicalFile.length() == fileSize && fileHash.equalsIgnoreCase(md5Hex(physicalFile))) {
                    //这些都通过表示文件是可用的 赋值并跳出循环
                    canUseFssObject = fssObject;
                    break;
                }
            }
            //起码找到了一个 可以使用的文件
            if (canUseFssObject != null) {
                var save = fssObjectService.add(copyFSSObject(fileName, canUseFssObject));
                //有可能有之前的残留临时文件 再此一并清除
                FileUtils.delete(getUploadTempPath(fileHash));
                //通知前台秒传成功
                return Result.ok().put("type", "upload-by-hash-success").put("item", save);
            }
        }
        //通知前台 没找到任何 和此 MD5 相同并且文件内容未损害的 文件
        return Result.fail("no-any-file-exists-for-hash");
    }


    @ScxRoute(value = "/info", methods = HttpMethod.POST)
    public BaseVo info(@FromBody String fssObjectID) {
        if (fssObjectID != null) {
            return Result.ok(fssObjectService.findByFSSObjectID(fssObjectID));
        } else {
            return Result.ok(null);
        }
    }

    @ScxRoute(value = "/list-info", methods = HttpMethod.POST)
    public BaseVo listInfo(@FromBody List<String> fssObjectIDs) {
        if (fssObjectIDs != null && fssObjectIDs.size() > 0) {
            return Result.ok(fssObjectService.findByFSSObjectIDs(fssObjectIDs));
        } else {
            return Result.ok(new ArrayList<>());
        }
    }

    private FSSObject checkFSSObjectID(String fssObjectID) {
        var fssObject = fssObjectService.findByFSSObjectID(fssObjectID);
        if (fssObject == null) {
            throw new NotFoundException();
        }
        return fssObject;
    }

}
