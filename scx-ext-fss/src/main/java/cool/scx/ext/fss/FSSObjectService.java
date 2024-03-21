package cool.scx.ext.fss;

import cool.scx.common.util.FileUtils;
import cool.scx.core.annotation.ScxService;
import cool.scx.core.base.BaseModelService;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.List;

import static cool.scx.data.QueryBuilder.*;
import static cool.scx.ext.fss.FSSHelper.getPhysicalFilePath;

/**
 * UploadFileService
 *
 * @author scx567888
 * @version 0.3.6
 */
@ScxService
public class FSSObjectService extends BaseModelService<FSSObject> {

    /**
     * 根据 hash 查找文件
     *
     * @param fileHash hash 值
     * @return 找的的数据
     */
    public List<FSSObject> findFSSObjectListByHash(String fileHash) {
        return find(query().where(eq("fileHash", fileHash)).orderBy(desc("uploadTime")));
    }

    public long countByHash(String fileHash) {
        return count(query().where(eq("fileHash", fileHash)));
    }

    public FSSObject findByFSSObjectID(String fssObjectID) {
        return get(query().where(eq("fssObjectID", fssObjectID)));
    }

    public List<FSSObject> findByFSSObjectIDs(List<String> fssObjectIDs) {
        return find(in("fssObjectID", fssObjectIDs));
    }

    /**
     * 根据 fssObjectID 进行删除 (同时还会删除物理文件, 如果引用为 0 的话)
     *
     * @param fssObjectID f
     * @throws java.io.IOException f
     */
    public void delete(String fssObjectID) throws IOException {
        //先获取文件的基本信息
        var needDeleteFile = this.findByFSSObjectID(fssObjectID);
        if (needDeleteFile != null) {
            //判断文件是否被其他人引用过
            long count = this.countByHash(needDeleteFile.fileHash);
            //没有被其他人引用过 可以删除物理文件
            if (count <= 1) {
                var filePath = getPhysicalFilePath(needDeleteFile);
                try {
                    FileUtils.delete(filePath.getParent());
                } catch (NoSuchFileException ignore) {
                    //文件不存在时忽略错误
                }
            }
            //删除数据库中的文件数据
            this.delete(needDeleteFile.id);
        }
    }

}
