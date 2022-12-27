package com.ichinae.samples.controller;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ichinae.samples.bean.User;
import com.ichinae.samples.common.constants.Constants;
import com.ichinae.samples.common.enums.GlobalDefaultDeleteEnum;
import com.ichinae.samples.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author fuchengwei
 * @date 2020/12/18 9:51 上午
 * @description 用户账号模块
 */
@Api(tags = "用户账号模块")
@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * 获取当前登录用户用户
     *
     * @return User
     */
    @ApiOperation("获取当前登录用户")
    @GetMapping("/getCurrentUser")
    public User getCurrentUser(HttpServletRequest request) {
        return (User) request.getAttribute(Constants.USER_INFO);
    }

    /**
     * 根据用户Id查询用户信息
     *
     * @param id 用户Id
     * @return User
     */
    @ApiOperation("根据用户Id获取用户信息")
    @ApiImplicitParam(name = "id", value = "用户id", paramType = "path", required = true)
    @GetMapping("/{id}")
    public User getUserByUserId(@PathVariable Integer id) {
        return userService.getUserByUserId(id);
    }

    /**
     * 根据用户名查询用户信息
     *
     * @param userName 用户名
     * @return User
     */
    @ApiOperation("根据用户名查询用户信息")
    @ApiImplicitParam(name = "userName", value = "用户名", required = true)
    @GetMapping("/getUserByUserName")
    public User getUserByUserName(String userName) {
        return userService.getUserByUserName(userName);
    }

    /**
     * 根据用户账号查询用户信息
     *
     * @param account 账号、邮箱、手机号
     * @return User
     */
    @ApiOperation("根据用户名查询用户信息")
    @ApiImplicitParam(name = "account", value = "账号、邮箱、手机号", required = true)
    @GetMapping("/getUserByAccount")
    public User getUserByAccount(String account) {
        return userService.getUserByAccount(account);
    }

    /**
     * 根据条件查询用户列表
     *
     * @param userName 用户名
     * @return List<User>
     */
    @ApiOperation("根据用户名查询用户列表")
    @ApiImplicitParam(name = "userName", value = "用户名", required = true)
    @GetMapping("/getUsersByUserName")
    public List<User> getUsersByUserName(String userName) {
        return userService.getUsersByUserName(userName);
    }

    /**
     * 根据关键字查询用户列表
     *
     * @param keyword  关键字
     * @param pageNum  当前页数
     * @param pageSize 每页条数
     * @return PageInfo<User>
     */
    @ApiOperation("根据关键字查询用户列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "关键字"),
            @ApiImplicitParam(name = "pageNum", value = "当前页数", defaultValue = "1"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", defaultValue = "10"),
    })
    @GetMapping("/getUsersByKeyword")
    public PageInfo<User> getUsersByKeyword(@RequestParam(value = "keyword", defaultValue = "", required = false) String keyword,
                                            @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                            @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> userList = userService.getUsersByKeyword(keyword);
        return new PageInfo<>(userList);
    }

    /**
     * 根据关键字查询用户手机号、邮箱是否已被注册
     * 传入用户Id的话就忽略该条用户Id
     *
     * @param keyword 手机号、邮箱
     * @param userId  用户Id
     * @return true：存在 false：不存在
     */
    @ApiOperation("根据关键字查询用户手机号、邮箱是否已被注册")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "手机号、邮箱", required = true),
            @ApiImplicitParam(name = "userId", value = "用户Id")
    })
    @GetMapping("/getIsExistsByKeyword")
    public Boolean getIsExistsByKeyword(String keyword, Integer userId) {
        // 字段验证
        Validator.validateNotEmpty(keyword, "关键字不能为空");

        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        criteria
                .orEqualTo("phone", keyword)
                .orEqualTo("email", keyword);
        if (ObjectUtil.isNotNull(userId)) {
            example.and(example
                    .createCriteria()
                    .andNotEqualTo("userId", userId));
        }
        return userService.isExists(example);
    }

    /**
     * 获取所有用户列表
     *
     * @return List<User>
     */
    @ApiOperation("获取所有用户列表")
    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }

    /**
     * 添加用户
     *
     * @param user    用户信息
     * @param roleIds 角色Id数组
     * @return String
     */
    @ApiOperation("添加用户")
    @ApiImplicitParam(name = "roleIds", value = "角色Id数组", required = true)
    @PostMapping
    public String insertUser(User user, Integer[] roleIds) {
        userService.insertUser(user, roleIds);
        return "添加成功";
    }

    /**
     * 修改用户
     *
     * @param user    用户信息
     * @param roleIds 角色Id
     * @return String
     */
    @ApiOperation("修改用户")
    @ApiImplicitParam(name = "roleIds", value = "角色Id数组", required = true)
    @PutMapping
    public String updateUser(User user, Integer[] roleIds) {
        userService.updateUser(user, roleIds);
        return "修改成功";
    }

    /**
     * 删除用户
     *
     * @param userIds 用户Id数组
     * @return String
     */
    @ApiOperation("删除用户")
    @ApiImplicitParam(name = "userIds", value = "用户Id数组", required = true)
    @DeleteMapping
    public String deleteUsers(Integer[] userIds) {
        // 字段验证
        Validator.validateNotEmpty(userIds, "用户Id不能为空");
        Integer[] roleIds = new Integer[0];

        for (Integer userId : userIds) {
            userService.updateUser(
                    new User()
                            .setUserId(userId)
                            .setIsDelete(GlobalDefaultDeleteEnum.DELETED.getCode()), roleIds
            );
        }
        return "删除成功";
    }

    /**
     * 添加用户权限
     *
     * @param id            用户Id
     * @param permissionIds 权限Id数组
     * @return String
     */
    @ApiOperation("添加用户权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户Id", paramType = "path", required = true),
            @ApiImplicitParam(name = "permissionIds", value = "权限Id数组")
    })
    @PostMapping("/{id}/permissions")
    public String insertUserPermissions(@PathVariable Integer id, Integer[] permissionIds) {
        userService.insertUserPermissions(id, permissionIds);
        return "配置用户权限成功";
    }
}
