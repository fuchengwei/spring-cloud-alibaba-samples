package com.github.samples.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author ChengWei.Fu
 * @date 2022/12/9
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Permission implements Serializable {
    /**
     * 主键id
     */
    private String id;

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
    @TableField(exist = false)
    private List<Permission> children;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
