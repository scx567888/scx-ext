package cool.scx.test.car;

import cool.scx.annotation.ScxModel;
import cool.scx.base.BaseModel;

import java.util.List;

/**
 * <p>Car class.</p>
 *
 * @author scx567888
 * @version 1.3.14
 * @since 1.3.14
 */
@ScxModel(tablePrefix = "test")
public class Car extends BaseModel {
    public String name;
    public List<Long> sizes;
    public List<String> tags;
    public boolean testBoolean;
}
