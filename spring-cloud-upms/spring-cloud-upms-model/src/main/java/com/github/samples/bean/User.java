package com.github.samples.bean;

import java.util.List;

/**
 * @author ChengWei.Fu
 * @date 2022/12/9
 */
public class User extends Base {
    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 账号
     */
    private String account;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 性别 0:未知 1:男 2:女
     */
    private Integer sex;

    /**
     * 账号状态 0:禁用 1:启用
     */
    private Integer status;

    /**
     * 部门id
     */
    private String departmentId;

    /**
     * 部门信息
     */
    private Department department;

    /**
     * 用户角色
     */
    private List<Role> roles;

    /**
     * 用户权限
     */
    private List<Permission> permissions;
}
