package cool.scx.test.car;

import cool.scx.annotation.Column;
import cool.scx.annotation.ScxModel;
import cool.scx.base.BaseModel;

import java.util.List;

/**
 * 汽车类
 */
@ScxModel(tablePrefix = "test")
public class Car extends BaseModel {

    /**
     * 汽车的图片
     */
    public String photo;

    /**
     * 汽车的名称
     */
    public String name;

    /**
     * 汽车的颜色
     */
    public String color;

    /**
     * 汽车品牌 id
     */
    public Long carBrandID;

    /**
     * 介绍
     */
    @Column(type = "TEXT")
    public String info;

    /**
     * 标签
     */
    public List<String> tags;

    /**
     * 汽车零部件 情况
     */
    public List<CarPartsInfo> carPartsInfoList;

    /**
     * 汽车零部件说明
     */
    public static class CarPartsInfo {

        /**
         * 部件名称
         */
        public String partName;

        /**
         * 部件类型
         */
        public String partType;

    }

}
