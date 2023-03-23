package cool.scx.ext.fss;

import net.coobird.thumbnailator.geometry.Positions;

import java.util.HashMap;
import java.util.Map;

public class FSSHelper {
    /**
     * type 和裁剪类型 映射表
     */
    private static final Map<String, Positions> TYPE_POSITIONS_MAP = new HashMap<>();

    static {
        TYPE_POSITIONS_MAP.put("top-left", Positions.TOP_LEFT);
        TYPE_POSITIONS_MAP.put("top-center", Positions.TOP_CENTER);
        TYPE_POSITIONS_MAP.put("top-right", Positions.TOP_RIGHT);
        TYPE_POSITIONS_MAP.put("center-left", Positions.CENTER_LEFT);
        TYPE_POSITIONS_MAP.put("center", Positions.CENTER);
        TYPE_POSITIONS_MAP.put("center-center", Positions.CENTER);
        TYPE_POSITIONS_MAP.put("center-right", Positions.CENTER_RIGHT);
        TYPE_POSITIONS_MAP.put("bottom-left", Positions.BOTTOM_LEFT);
        TYPE_POSITIONS_MAP.put("bottom-center", Positions.BOTTOM_CENTER);
        TYPE_POSITIONS_MAP.put("bottom-right", Positions.BOTTOM_RIGHT);
        //简写
        TYPE_POSITIONS_MAP.put("tl", Positions.TOP_LEFT);
        TYPE_POSITIONS_MAP.put("tc", Positions.TOP_CENTER);
        TYPE_POSITIONS_MAP.put("tr", Positions.TOP_RIGHT);
        TYPE_POSITIONS_MAP.put("cl", Positions.CENTER_LEFT);
        TYPE_POSITIONS_MAP.put("c", Positions.CENTER);
        TYPE_POSITIONS_MAP.put("cc", Positions.CENTER);
        TYPE_POSITIONS_MAP.put("cr", Positions.CENTER_RIGHT);
        TYPE_POSITIONS_MAP.put("bl", Positions.BOTTOM_LEFT);
        TYPE_POSITIONS_MAP.put("bc", Positions.BOTTOM_CENTER);
        TYPE_POSITIONS_MAP.put("br", Positions.BOTTOM_RIGHT);
    }

    public static Positions getPositions(String type) {
        return TYPE_POSITIONS_MAP.get(type.toLowerCase());
    }

}
