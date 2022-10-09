package cool.scx.ext.fss;

import cool.scx.core.annotation.ScxService;
import cool.scx.core.base.BaseModelService;
import cool.scx.sql.base.Query;

import java.util.List;

/**
 * UploadFileService
 *
 * @author scx567888
 * @version 0.3.6
 */
@ScxService
public class FSSObjectService extends BaseModelService<FSSObject> {

    /**
     * 根据 md5 查找文件
     *
     * @param fileMD5 md5 值
     * @return 找的的数据
     */
    public List<FSSObject> findFSSObjectListByMD5(String fileMD5) {
        return list(new Query().equal("fileMD5", fileMD5).desc("uploadTime"));
    }

    /**
     * a
     *
     * @param fileMD5 a
     * @return a
     */
    public long countByMD5(String fileMD5) {
        return count(new Query().equal("fileMD5", fileMD5));
    }

    /**
     * <p>checkFileID.</p>
     *
     * @param fssObjectID a {@link java.lang.String} object
     * @return a {@link cool.scx.ext.fss.FSSObject} object
     */
    public FSSObject findByFSSObjectID(String fssObjectID) {
        return get(new Query().equal("fssObjectID", fssObjectID));
    }

    /**
     * a
     *
     * @param fssObjectIDs a
     * @return a
     */
    public List<FSSObject> findByFSSObjectIDs(List<String> fssObjectIDs) {
        return list(new Query().in("fssObjectID", fssObjectIDs));
    }

}
