package com.ichinae.samples.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author fuchengwei
 * @date 2020/12/19 5:09 下午
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@ApiModel("权限实体")
@Table(name = "permission")
public class Permission implements Serializable {
    /**
     * 权限Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("权限Id")
    private Integer permissionId;

    /**
     * 权限名称
     */
    @ApiModelProperty("权限名称")
    private String permissionName;

    /**
     * 权限值
     */
    @ApiModelProperty("权限值")
    private String permissionValue;

    /**
     * 权限类型 1：目录 2：菜单 3：按钮
     */
    @ApiModelProperty("权限类型 1：目录 2：菜单 3：按钮")
    private Integer permissionType;

    /**
     * 权限类型名称
     */
    @ApiModelProperty("权限类型名称")
    @Transient
    private String permissionTypeName;

    /**
     * 权限状态 0：启用 1：禁用
     */
    @ApiModelProperty("权限状态 0：启用 1：禁用")
    private Integer permissionStatus;

    /**
     * 权限状态名称
     */
    @ApiModelProperty("权限状态名称")
    @Transient
    private String permissionStatusName;

    /**
     * 菜单图标
     */
    @ApiModelProperty("菜单图标")
    private String icon;

    /**
     * 菜单路径
     */
    @ApiModelProperty("菜单路径")
    private String url;

    /**
     * 组件地址
     */
    @ApiModelProperty("组件地址")
    private String componentUrl;

    /**
     * 跳转地址
     */
    @ApiModelProperty("跳转地址")
    private String redirectUrl;

    /**
     * 排序
     */
    @ApiModelProperty("排序")
    private Integer sort;

    /**
     * 所属上级
     */
    @ApiModelProperty("所属上级")
    private Integer superiorId;

    /**
     * 是否显示: 0 显示 1 不显示
     */
    @ApiModelProperty("是否显示: 0：显示 1：不显示")
    private Integer display;

    /**
     * 子节点信息
     */
    @ApiModelProperty("子节点信息")
    private List<Permission> children;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @ApiModelProperty("修改时间")
    private LocalDateTime updateTime;
}
