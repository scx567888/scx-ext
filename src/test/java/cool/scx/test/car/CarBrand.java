package cool.scx.test.car;

import cool.scx.annotation.ScxModel;
import cool.scx.base.BaseModel;

/**
 * 汽车品牌
 */
@ScxModel(tablePrefix = "test")
public class CarBrand extends BaseModel {

    /**
     * 汽车品牌名称
     */
    public String name;

}
