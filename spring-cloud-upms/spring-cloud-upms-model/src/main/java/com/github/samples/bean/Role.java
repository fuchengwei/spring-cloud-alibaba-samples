package com.github.samples.bean;

import java.util.List;

/**
 * @author ChengWei.Fu
 * @date 2022/12/9
 */
public class Role extends Base {
    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 角色权限
     */
    private List<Permission> permissions;
}
