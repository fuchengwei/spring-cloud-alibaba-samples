package com.ichinae.samples.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author fuchengwei
 * @date 2020/12/16 5:28 下午
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Table(name = "user")
@ApiModel(description = "用户实体")
public class User implements Serializable {
    /**
     * 用户Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("用户Id")
    private Integer userId;

    /**
     * 用户名
     */
    @ApiModelProperty("用户名")
    private String userName;

    /**
     * 密码
     */
    @ApiModelProperty("密码")
    private String password;

    /**
     * 账号
     */
    @ApiModelProperty("账号")
    private String account;

    /**
     * 手机号
     */
    @ApiModelProperty("手机号")
    private String phone;

    /**
     * 邮箱
     */
    @ApiModelProperty("邮箱")
    private String email;

    /**
     * 性别：0 未知 1 男 2女
     */
    @ApiModelProperty("性别：0 未知 1 男 2女")
    private Integer sex;

    /**
     * 账号状态：0 启用 1 禁用
     */
    @ApiModelProperty("账号状态：0 启用 1 禁用")
    private Integer status;

    /**
     * 是否删除：0 已删除 1 未删除
     */
    @ApiModelProperty("是否删除：0 已删除 1 未删除")
    private Integer isDelete;

    /**
     * 部门Id
     */
    @ApiModelProperty("部门Id")
    private String departmentId;

    /**
     * 部门信息
     */
    @ApiModelProperty("部门信息")
    private Department department;

    /**
     * 用户角色
     */
    @ApiModelProperty("用户角色")
    private List<Role> roles;

    /**
     * 用户权限
     */
    @ApiModelProperty("用户权限")
    private List<Permission> permissions;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
}
