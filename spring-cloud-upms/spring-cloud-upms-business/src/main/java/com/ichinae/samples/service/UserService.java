package com.ichinae.samples.service;

import com.ichinae.samples.bean.User;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author fuchengwei
 * @date 2020/12/25 6:00 下午
 */
public interface UserService {
    /**
     * 根据用户id查询用户
     *
     * @param userId 用户id
     * @return user
     */
    User getUserByUserId(Integer userId);

    /**
     * 根据用户名查询用户
     *
     * @param userName 用户名
     * @return user
     */
    User getUserByUserName(String userName);

    /**
     * 根据用户账号查询用户
     *
     * @param account 用户账号
     * @return user
     */
    User getUserByAccount(String account);

    /**
     * 根据用户名查询用户列表
     *
     * @param userName 用户名
     * @return list
     */
    List<User> getUsersByUserName(String userName);

    /**
     * 根据关键字查询用户列表
     *
     * @param keyword 关键字
     * @return list
     */
    List<User> getUsersByKeyword(String keyword);

    /**
     * 根据条件判断用户是否存在
     *
     * @param example 条件
     * @return true: 存在 false: 不存在
     */
    Boolean isExists(Example example);

    /**
     * 获取所有用户列表
     *
     * @return List<User>
     */
    List<User> getUsers();

    /**
     * 添加用户
     *
     * @param user    用户信息
     * @param roleIds 角色Id数组
     */
    void insertUser(User user, Integer[] roleIds);

    /**
     * 修改用户
     *
     * @param user    用户信息
     * @param roleIds 角色Id数组
     */
    void updateUser(User user, Integer[] roleIds);

    /**
     * 添加用户权限
     *
     * @param userId        用户Id
     * @param permissionIds 权限Id数组
     */
    void insertUserPermissions(Integer userId, Integer[] permissionIds);
}
