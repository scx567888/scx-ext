package cool.scx.ext.fss;

import cool.scx.annotation.ScxService;
import cool.scx.base.BaseService;
import cool.scx.bo.Query;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;

/**
 * UploadFileService
 *
 * @author scx567888
 * @version 0.3.6
 */
@ScxService
public class FSSObjectService extends BaseService<FSSObject> {

    public static Path getPhysicalFilePath(FSSObject fssObject) {
        return Path.of(FSSConfig.uploadFilePath().getPath(), fssObject.filePath);
    }

    /**
     * 根据 md5 查找文件
     *
     * @param fileMD5 md5 值
     * @return 找的的数据
     * @throws SQLException s
     */
    public List<FSSObject> findFSSObjectListByMd5(String fileMD5) throws SQLException {
        return list(new Query().equal("fileMD5", fileMD5).desc("uploadTime"));
    }

    /**
     * <p>checkFileID.</p>
     *
     * @param fssObjectID a {@link java.lang.String} object
     * @return a {@link cool.scx.ext.fss.FSSObject} object
     * @throws SQLException if any.
     */
    public FSSObject findByFSSObjectID(String fssObjectID) throws SQLException {
        return get(new Query().equal("fssObjectID", fssObjectID));
    }

    public List<FSSObject> findByFSSObjectIDs(List<String> fssObjectIDs) throws SQLException {
        return list(new Query().in("fssObjectID", fssObjectIDs));
    }

}
