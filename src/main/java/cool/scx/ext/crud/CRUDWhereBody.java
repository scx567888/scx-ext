package cool.scx.ext.crud;

public class CRUDWhereBody {

    /**
     * 字段名称 (注意不是数据库名称)
     */
    public String fieldName;

    /**
     * 类型
     */
    public String whereType;

    /**
     * 因为参数不固定 所以这里用两个参数
     * 参数1
     */
    public Object value1;

    /**
     * 参数2
     */
    public Object value2;

}