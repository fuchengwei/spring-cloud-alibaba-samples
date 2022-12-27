package com.ichinae.samples.service;

import com.ichinae.samples.bean.Permission;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author fuchengwei
 * @date 2020/12/22 8:17 下午
 */
public interface PermissionService {
    /**
     * 根据关键字判断权限是否存在
     *
     * @param keyword      权限名称、权限值
     * @param permissionId 权限Id
     * @return true: 存在 false: 不存在
     */
    Boolean isExists(String keyword, Integer permissionId);

    /**
     * 根据条件查询权限
     *
     * @param example 条件
     * @return Permission
     */
    Permission getPermissionByExample(Example example);

    /**
     * 根据条件查询权限集合
     *
     * @param example     条件
     * @param isRecursive 是否递归
     * @return List<Permission>
     */
    List<Permission> getPermissionsByExample(Example example, boolean isRecursive);

    /**
     * 更新权限排序
     *
     * @param permissionIds 需要更新的权限Ids
     */
    void updatePermissionSort(Integer[] permissionIds);

    /**
     * 添加权限
     *
     * @param permission 权限信息
     */
    void insertPermission(Permission permission);

    /**
     * 更新权限
     *
     * @param permission 权限信息
     */
    void updatePermission(Permission permission);

    /**
     * 删除权限
     *
     * @param permissionIds 权限Id数组
     */
    void deletePermissions(Integer[] permissionIds);
}
