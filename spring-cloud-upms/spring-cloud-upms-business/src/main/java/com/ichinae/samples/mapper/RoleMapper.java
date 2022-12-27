package com.ichinae.samples.mapper;

import com.ichinae.samples.bean.Permission;
import com.ichinae.samples.bean.Role;
import org.apache.ibatis.annotations.CacheNamespaceRef;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author fuchengwei
 * @date 2020/12/23 5:39 下午
 */
@CacheNamespaceRef(RoleMapper.class)
public interface RoleMapper extends Mapper<Role> {
    /**
     * 根据角色名称查询角色集合
     *
     * @param roleName 角色名称
     * @return list
     */
    List<Role> getRolesByRoleName(@Param("roleName") String roleName);

    /**
     * 根据角色Id查询权限集合
     *
     * @param roleId 角色Id
     * @return list
     */
    List<Permission> getPermissionsByRoleId(@Param("roleId") Integer roleId);

    /**
     * 添加角色权限
     *
     * @param roleId       角色Id
     * @param permissionId 权限Id
     * @return int
     */
    int insertRolePermission(@Param("roleId") Integer roleId, @Param("permissionId") Integer permissionId);

    /**
     * 删除角色权限
     *
     * @param roleId       角色Id
     * @param permissionId 权限Id
     */
    void deleteRolePermission(@Param("roleId") Integer roleId, @Param("permissionId") Integer permissionId);
}
