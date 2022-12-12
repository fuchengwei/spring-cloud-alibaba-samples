package com.github.samples.bean;

import java.util.List;

/**
 * @author ChengWei.Fu
 * @date 2022/12/9
 */
public class Permission extends Base {
    /**
     * 权限名称
     */
    private String permissionName;

    /**
     * 权限值
     */
    private String permissionValue;

    /**
     * 权限类型 1:目录 2:菜单 3:按钮
     */
    private Integer permissionType;

    /**
     * 权限类型名称
     */
    private String permissionTypeName;

    /**
     * 权限状态 0:禁用 1:启用
     */
    private Integer permissionStatus;

    /**
     * 权限状态名称
     */
    private String permissionStatusName;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 菜单路径
     */
    private String url;

    /**
     * 组件路径
     */
    private String componentUrl;

    /**
     * 重定向路径
     */
    private String redirectUrl;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 父级id
     */
    private String parentId;

    /**
     * 子节点信息
     */
    private List<Permission> children;
}
