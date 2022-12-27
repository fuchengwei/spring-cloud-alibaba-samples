package com.ichinae.samples.service.impl;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.ichinae.samples.bean.User;
import com.ichinae.samples.common.enums.SystemExceptionEnum;
import com.ichinae.samples.common.enums.UpmsExceptionEnum;
import com.ichinae.samples.common.exception.SystemException;
import com.ichinae.samples.common.utils.AccountUtil;
import com.ichinae.samples.mapper.UserMapper;
import com.ichinae.samples.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author fuchengwei
 * @date 2020/12/25 6:02 下午
 */
@Service
@Transactional(rollbackFor = Exception.class)
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;

    @Override
    public User getUserByUserId(Integer userId) {
        User user = userMapper.getUserByUserId(userId);
        if (ObjectUtil.isNull(user)) {
            throw new SystemException(UpmsExceptionEnum.USER_ID_EXCEPTION);
        }
        return user;
    }

    @Override
    public User getUserByUserName(String userName) {
        // 字段验证
        Validator.validateNotEmpty(userName, "用户名不能为空");

        User user = userMapper.getUserByUserName(userName);

        if (ObjectUtil.isNull(user)) {
            throw new SystemException(UpmsExceptionEnum.USER_ACCOUNT_EXCEPTION);
        }
        return user;
    }

    @Override
    public User getUserByAccount(String account) {
        // 字段验证
        Validator.validateNotEmpty(account, "账号不能为空");

        User user = userMapper.getUserByAccount(account);

        if (ObjectUtil.isNull(user)) {
            throw new SystemException(UpmsExceptionEnum.USER_ACCOUNT_EXCEPTION);
        }
        return user;
    }

    @Override
    public List<User> getUsersByUserName(String userName) {
        // 字段验证
        Validator.validateNotEmpty(userName, "用户名不能为空");

        return userMapper.getUsersByUserName(userName);
    }

    @Override
    public List<User> getUsersByKeyword(String keyword) {
        return userMapper.getUsersByKeyword(keyword);
    }

    @Override
    public Boolean isExists(Example example) {
        return ObjectUtil.isNotNull(userMapper.selectOneByExample(example));
    }

    @Override
    public List<User> getUsers() {
        return userMapper.selectAll();
    }

    @Override
    public void insertUser(User user, Integer[] roleIds) {
        // 字段验证
        Validator.validateNotEmpty(user.getUserName(), "用户名不能为空");
        Validator.validateNotEmpty(user.getPhone(), "手机号不能为空");
        Validator.validateMobile(user.getPhone(), "请输入正确手机号");
        Validator.validateNotEmpty(roleIds, "角色ID不能为空");
        Validator.validateNotEmpty(user.getDepartmentId(), "部门ID不能为空");

        String account;
        Boolean isExists;

        // 验证手机号是否已被注册
        Example example = new Example(User.class);
        example
                .createCriteria()
                .andEqualTo("phone", user.getPhone());
        isExists = isExists(example);
        if (isExists) {
            throw new SystemException(UpmsExceptionEnum.PHONE_REGISTERED_EXCEPTION);
        }

        do {
            // 随机生成账号
            account = AccountUtil.getRandomAccount();

            // 清空查询条件
            example.clear();

            // 查找账号是否已经存在
            example
                    .createCriteria()
                    .andEqualTo("account", account);
            isExists = isExists(example);
        } while (isExists);

        // 生成密码加密后的密文
        String password = DigestUtil.bcrypt(user.getPhone());

        user
                .setAccount(account)
                .setPassword(password);

        // 存入数据库
        int result = userMapper.insertSelective(user);
        if (result == 0) {
            throw new SystemException(SystemExceptionEnum.MYSQL_INSERT_EXCEPTION);
        } else {
            // 添加用户角色
            insertUserRole(user.getUserId(), roleIds);
        }
    }

    @Override
    public void updateUser(User user, Integer[] roleIds) {
        // 字段验证
        Validator.validateNotEmpty(user.getUserId(), "用户Id不能为空");

        // 判断手机号不为空的情况下验证手机号是否已被注册
        if (StrUtil.isNotBlank(user.getPhone())) {
            Example example = new Example(User.class);
            example
                    .createCriteria()
                    .orEqualTo("phone", user.getPhone());

            // 判断邮箱不为空的情况下验证邮箱是否已被注册
            if (StrUtil.isNotBlank(user.getEmail())) {
                example.and(example
                        .createCriteria()
                        .orEqualTo("email", user.getEmail()));
            }

            // 排除当前用户
            example.and(example
                    .createCriteria()
                    .andNotEqualTo("userId", user.getUserId()));
            Boolean isExists = isExists(example);
            if (isExists) {
                throw new SystemException(UpmsExceptionEnum.PHONE_EMAIL_REGISTERED_EXCEPTION);
            }
        }

        user.setUpdateTime(null);

        // 存入数据库
        int result = userMapper.updateByPrimaryKeySelective(user);
        if (result == 0) {
            throw new SystemException(SystemExceptionEnum.MYSQL_UPDATE_EXCEPTION);
        } else {
            insertUserRole(user.getUserId(), roleIds);
        }
    }

    @Override
    public void insertUserPermissions(Integer userId, Integer[] permissionIds) {
        // 字段验证
        Validator.validateNotEmpty(userId, "用户Id不能为空");

        userMapper.deleteUserPermission(userId, null);

        if (ObjectUtil.isNotNull(permissionIds)) {
            for (Integer permissionId : permissionIds) {
                int result = userMapper.insertUserPermission(userId, permissionId);
                if (result == 0) {
                    throw new SystemException(SystemExceptionEnum.MYSQL_INSERT_EXCEPTION);
                }
            }
        }
    }

    private void insertUserRole(Integer userId, Integer[] roleIds) {
        // 字段验证
        Validator.validateNotEmpty(userId, "用户Id不能为空");
        Validator.validateNotEmpty(roleIds, "角色Id不能为空");

        userMapper.deleteUserRole(userId, null);

        for (Integer roleId : roleIds) {
            int result = userMapper.insertUserRole(userId, roleId);
            if (result == 0) {
                throw new SystemException(SystemExceptionEnum.MYSQL_INSERT_EXCEPTION);
            }
        }
    }
}
