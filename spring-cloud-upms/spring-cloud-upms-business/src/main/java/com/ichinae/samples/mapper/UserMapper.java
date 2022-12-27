package com.ichinae.samples.mapper;

import com.ichinae.samples.bean.Permission;
import com.ichinae.samples.bean.Role;
import com.ichinae.samples.bean.User;
import org.apache.ibatis.annotations.CacheNamespaceRef;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author fuchengwei
 * @date 2020/12/18 2:57 下午
 */
@CacheNamespaceRef(UserMapper.class)
public interface UserMapper extends Mapper<User> {
    /**
     * 根据用户id查询用户
     *
     * @param userId 用户Id
     * @return user
     */
    User getUserByUserId(@Param("userId") Integer userId);

    /**
     * 根据用户账号查询用户
     *
     * @param account 账号、邮箱、手机号
     * @return user
     */
    User getUserByAccount(@Param("account") String account);

    /**
     * 根据用户名查询用户
     *
     * @param userName 用户名
     * @return user
     */
    User getUserByUserName(@Param("userName") String userName);

    /**
     * 根据用户名查询用户列表
     *
     * @param userName 用户名
     * @return list
     */
    List<User> getUsersByUserName(@Param("userName") String userName);

    /**
     * 根据关键字查询用户列表
     *
     * @param keyword 账号、用户名、邮箱、手机号
     * @return list
     */
    List<User> getUsersByKeyword(@Param("keyword") String keyword);

    /**
     * 根据用户id查询用户角色集合
     *
     * @param userId 用户Id
     * @return list
     */
    List<Role> getRoleByUserId(@Param("userId") Integer userId);

    /**
     * 根据用户id查询用户权限集合
     *
     * @param userId 用户Id
     * @return list
     */
    List<Permission> getPermissionByUserId(@Param("userId") Integer userId);

    /**
     * 根据角色id查询角色权限集合
     *
     * @param roleId 角色Id
     * @return list
     */
    List<Permission> getPermissionByRoleId(@Param("roleId") Integer roleId);

    /**
     * 添加用户角色
     *
     * @param userId 用户Id
     * @param roleId 角色Id
     * @return int
     */
    int insertUserRole(@Param("userId") Integer userId, @Param("roleId") Integer roleId);

    /**
     * 添加用户权限
     *
     * @param userId       用户Id
     * @param permissionId 权限Id
     * @return int
     */
    int insertUserPermission(@Param("userId") Integer userId, @Param("permissionId") Integer permissionId);

    /**
     * 删除用户角色
     *
     * @param userId 用户Id
     * @param roleId 角色Id
     * @return int
     */
    int deleteUserRole(@Param("userId") Integer userId, @Param("roleId") Integer roleId);

    /**
     * 删除用户权限
     *
     * @param userId       用户Id
     * @param permissionId 权限Id
     * @return int
     */
    int deleteUserPermission(@Param("userId") Integer userId, @Param("permissionId") Integer permissionId);
}
