package cool.scx.ext.organization.auth;

import cool.scx.base.BaseModel;

import java.util.List;

/**
 * 若 model 需要存储权限可以继承此类 提供四个权限存储字段
 * 权限实体类
 */
public abstract class PermsModel extends BaseModel {

    /**
     * 具体业务权限 (后台用)
     */
    public List<String> perms;

    /**
     * 前台页面权限
     */
    public List<String> pagePerms;

    /**
     * 前台页面元素权限 (如 输入框, 按钮 等)
     */
    public List<String> pageElementPerms;

    /**
     * api 访问权限
     */
    public List<String> apiPerms;

}
