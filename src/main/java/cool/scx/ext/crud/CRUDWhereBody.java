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

    /**
     * 便于序列化
     */
    public CRUDWhereBody() {

    }

    /**
     * 便于开发人员使用
     *
     * @param fieldName f
     * @param whereType w
     * @param value1    v
     * @param value2    v
     */
    public CRUDWhereBody(String fieldName, String whereType, Object value1, Object value2) {
        this.fieldName = fieldName;
        this.whereType = whereType;
        this.value1 = value1;
        this.value2 = value2;
    }

}