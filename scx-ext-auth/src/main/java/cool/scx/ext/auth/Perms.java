package cool.scx.ext.auth;

import java.util.Set;

/**
 * 权限包装器
 *
 * @param perms            通用权限
 * @param pagePerms        页面权限(路由权限)
 * @param pageElementPerms 页面元素权限
 * @param apiPerms         api 权限 {@link cool.scx.ext.auth.annotation.ApiPerms}
 */
public record Perms(Set<String> perms, Set<String> pagePerms, Set<String> pageElementPerms,
                    Set<String> apiPerms) {

}