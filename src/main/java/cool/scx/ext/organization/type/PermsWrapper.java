package cool.scx.ext.organization.type;

import java.util.Set;

/**
 * 权限包装器
 */
public record PermsWrapper(Set<String> perms, Set<String> pagePerms, Set<String> pageElementPerms,
                           Set<String> apiPerms) {

}