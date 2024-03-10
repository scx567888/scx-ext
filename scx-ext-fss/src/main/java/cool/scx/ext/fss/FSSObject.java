package cool.scx.ext.fss;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cool.scx.core.base.BaseModel;
import cool.scx.data.jdbc.annotation.Column;
import cool.scx.data.jdbc.annotation.DataType;
import cool.scx.data.jdbc.annotation.Table;

import java.time.LocalDateTime;

import static cool.scx.jdbc.standard.StandardDataType.TEXT;

/**
 * 文件上传表
 *
 * @author scx567888
 * @version 0.3.6
 */
@Table
public class FSSObject extends BaseModel {

    /**
     * 这里为了防止用户可以根据 id 猜测出来文件 业务中不使用 BaseModel 的 id
     */
    @Column(index = true, unique = true, notNull = true)
    public String fssObjectID;

    /**
     * 文件存储的路径 (相对与上传根目录的)
     */
    @JsonIgnore
    public String[] filePath;

    /**
     * 文件的大小 (格式化后的 就是人能看懂的那种)
     */
    public String fileSizeDisplay;

    /**
     * 文件的大小 long
     */
    public Long fileSize;

    /**
     * 原始文件名
     */
    @Column(dataType = @DataType(TEXT), notNull = true)
    public String fileName;

    /**
     * 上传日期
     */
    public LocalDateTime uploadTime;

    /**
     * 文件的 hash 值 (目前采用 md5)
     */
    @Column(index = true, notNull = true)
    public String fileHash;

    /**
     * 文件拓展名
     */
    @Column(dataType = @DataType(TEXT))
    public String fileExtension;

}
