package cool.scx.ext.organization.auth;

import cool.scx.core.ScxContext;
import cool.scx.core.annotation.ScxMapping;
import cool.scx.core.enumeration.HttpMethod;
import cool.scx.core.vo.BaseVo;
import cool.scx.core.vo.DataJson;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>PermFlagController class.</p>
 *
 * @author scx567888
 * @version 1.14.5
 */
@ScxMapping("api/perm-flag")
public class PermFlagController {

    private final List<PermFlag> permFlags;

    private final BaseVo vo;

    /**
     * <p>Constructor for PermFlagController.</p>
     */
    public PermFlagController() {
        permFlags = initPermFlags();
        vo = DataJson.ok().data(permFlags.stream().map(c -> Map.of("description", c.description(), "permString", c.permString())).toList());
    }

    /**
     * <p>list.</p>
     *
     * @return a {@link cool.scx.core.vo.BaseVo} object
     */
    @ScxMapping(method = HttpMethod.GET)
    public BaseVo list() {
        return vo;
    }

    /**
     * <p>initPermFlags.</p>
     *
     * @return a {@link java.util.List} object
     */
    @SuppressWarnings("unchecked")
    private List<PermFlag> initPermFlags() {
        var permClassList = Arrays.stream(ScxContext.scxModules())
                .flatMap(c -> c.classList().stream())
                .filter(d -> PermFlag.class.isAssignableFrom(d) && !d.isInterface())
                .map(d -> (Class<PermFlag>) d)
                .toList();

        return permClassList.stream()
                .flatMap(c -> c.isEnum() ?
                        Stream.of(c.getEnumConstants()) :
                        Stream.of(ScxContext.getBean(c)))
                .collect(Collectors.toList());
    }

    /**
     * <p>permFlags.</p>
     *
     * @return a {@link java.util.List} object
     */
    public List<PermFlag> permFlags() {
        return permFlags;
    }

}
