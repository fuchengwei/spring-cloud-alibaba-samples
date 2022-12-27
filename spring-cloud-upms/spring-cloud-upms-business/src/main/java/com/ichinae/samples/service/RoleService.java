package com.ichinae.samples.service;

import com.ichinae.samples.bean.Role;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author fuchengwei
 * @date 2020/12/25 2:15 下午
 */
public interface RoleService {
    /**
     * 根据条件查询角色信息
     *
     * @param example 条件
     * @return role
     */
    Role getRoleByExample(Example example);

    /**
     * 根据角色名称查询角色集合
     *
     * @param roleName 角色名称
     * @return list
     */
    List<Role> getRolesByRoleName(String roleName);

    /**
     * 查询所有角色
     *
     * @return list
     */
    List<Role> getRoles();

    /**
     * 根据角色名称查询角色是否存在
     *
     * @param roleName 角色名称
     * @param roleId   角色Id
     * @return true: 存在 false: 不存在
     */
    Boolean isExists(String roleName, Integer roleId);

    /**
     * 添加角色
     *
     * @param role 角色信息
     */
    void insertRole(Role role);

    /**
     * 修改角色
     *
     * @param role 更新角色
     */
    void updateRole(Role role);

    /**
     * 删除角色
     *
     * @param roleIds 角色列表
     */
    void deleteRoles(Integer[] roleIds);

    /**
     * 添加角色权限
     *
     * @param roleId        角色Id
     * @param permissionIds 权限Id数组
     */
    void insertRolePermission(Integer roleId, Integer[] permissionIds);
}
