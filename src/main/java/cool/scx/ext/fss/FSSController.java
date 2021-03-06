package cool.scx.ext.fss;

import cool.scx.core.ScxContext;
import cool.scx.core.annotation.*;
import cool.scx.core.enumeration.HttpMethod;
import cool.scx.core.type.UploadedEntity;
import cool.scx.core.vo.*;

import java.io.IOException;
import java.util.List;

/**
 * <p>FSSController class.</p>
 *
 * @author scx567888
 * @version 0.3.6
 */
@ScxMapping("/api/fss")
public class FSSController {

    private final FSSHandler fssHandler;

    /**
     * 构造函数
     */
    public FSSController() {
        var fssHandlerClass = ScxContext.findScxModule(FSSModule.class).fssHandlerClass();
        this.fssHandler = ScxContext.getBean(fssHandlerClass);
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
        return fssHandler.download(fssObjectID);
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
        return fssHandler.image(fssObjectID, width, height, type);
    }

    /**
     * 展示文件
     *
     * @param fssObjectID id
     * @return a {@link cool.scx.core.vo.Raw} object.
     */
    @ScxMapping(value = "/raw/:fssObjectID", method = {HttpMethod.GET, HttpMethod.HEAD})
    public Raw raw(@FromPath String fssObjectID) {
        return fssHandler.raw(fssObjectID);
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
        return fssHandler.upload(fileName, fileSize, fileMD5, chunkLength, nowChunkIndex, fileData);
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
        return fssHandler.delete(fssObjectID);
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
        return fssHandler.checkAnyFileExistsByThisMD5(fileName, fileSize, fileMD5);
    }

    /**
     * <p>listFile.</p>
     *
     * @param fssObjectID a {@link java.util.Map} object.
     * @return a {@link cool.scx.core.vo.Json} object.
     */
    @ScxMapping(value = "/info", method = HttpMethod.POST)
    public BaseVo info(@FromBody String fssObjectID) {
        return fssHandler.info(fssObjectID);
    }

    /**
     * s
     *
     * @param fssObjectIDs a
     * @return a
     */
    @ScxMapping(value = "/list-info", method = HttpMethod.POST)
    public BaseVo listInfo(@FromBody List<String> fssObjectIDs) {
        return fssHandler.listInfo(fssObjectIDs);
    }

}
