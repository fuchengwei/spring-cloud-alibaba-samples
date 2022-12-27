package com.ichinae.samples.service.impl;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ObjectUtil;
import com.ichinae.samples.bean.Role;
import com.ichinae.samples.common.enums.SystemExceptionEnum;
import com.ichinae.samples.common.enums.UpmsExceptionEnum;
import com.ichinae.samples.common.exception.SystemException;
import com.ichinae.samples.mapper.RoleMapper;
import com.ichinae.samples.mapper.UserMapper;
import com.ichinae.samples.service.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author fuchengwei
 * @date 2020/12/25 2:25 下午
 */
@Service
@Transactional(rollbackFor = Exception.class)
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleMapper roleMapper;
    private final UserMapper userMapper;

    @Override
    public Role getRoleByExample(Example example) {
        return roleMapper.selectOneByExample(example);
    }

    @Override
    public List<Role> getRolesByRoleName(String roleName) {
        return roleMapper.getRolesByRoleName(roleName);
    }

    @Override
    public List<Role> getRoles() {
        return roleMapper.selectAll();
    }

    @Override
    public Boolean isExists(String roleName, Integer roleId) {
        // 字段验证
        Validator.validateNotEmpty(roleName, "角色名称不能为空");

        Example example = new Example(Role.class);
        example
                .createCriteria()
                .orEqualTo("roleName", roleName);
        if (ObjectUtil.isNotNull(roleId)) {
            example.and(example
                    .createCriteria()
                    .andNotEqualTo("roleId", roleId));
        }
        return ObjectUtil.isNotNull(getRoleByExample(example));
    }

    @Override
    public void insertRole(Role role) {
        // 字段验证
        Validator.validateNotEmpty(role.getRoleName(), "角色名称不能为空");
        Validator.validateNotEmpty(role.getDescription(), "角色描述不能为空");

        // 判断角色名称是否存在
        if (isExists(role.getRoleName(), null)) {
            throw new SystemException(UpmsExceptionEnum.ROLE_NAME_EXCEPTION);
        }

        // 存入数据库
        int result = roleMapper.insertSelective(role);
        if (result == 0) {
            throw new SystemException(SystemExceptionEnum.MYSQL_INSERT_EXCEPTION);
        }
    }

    @Override
    public void updateRole(Role role) {
        // 字段验证
        Validator.validateNotEmpty(role.getRoleId(), "角色id不能为空");

        // 判断角色名称是否存在
        if (isExists(role.getRoleName(), role.getRoleId())) {
            throw new SystemException(UpmsExceptionEnum.ROLE_NAME_EXCEPTION);
        }

        role.setUpdateTime(null);

        // 存入数据库
        int result = roleMapper.updateByPrimaryKeySelective(role);
        if (result == 0) {
            throw new SystemException(SystemExceptionEnum.MYSQL_UPDATE_EXCEPTION);
        }
    }

    @Override
    public void deleteRoles(Integer[] roleIds) {
        for (Integer roleId : roleIds) {
            int resultRole = roleMapper.deleteByPrimaryKey(roleId);
            if (resultRole == 0) {
                throw new SystemException(SystemExceptionEnum.MYSQL_DELETE_EXCEPTION);
            } else {
                userMapper.deleteUserRole(null, roleId);
                roleMapper.deleteRolePermission(roleId, null);
            }
        }
    }

    @Override
    public void insertRolePermission(Integer roleId, Integer[] permissionIds) {
        // 字段验证
        Validator.validateNotEmpty(roleId, "角色id不能为空");

        roleMapper.deleteRolePermission(roleId, null);

        if (ObjectUtil.isNotNull(permissionIds)) {
            for (Integer permissionId : permissionIds) {
                int result = roleMapper.insertRolePermission(roleId, permissionId);

                if (result == 0) {
                    throw new SystemException(SystemExceptionEnum.MYSQL_INSERT_EXCEPTION);
                }
            }
        }
    }
}
