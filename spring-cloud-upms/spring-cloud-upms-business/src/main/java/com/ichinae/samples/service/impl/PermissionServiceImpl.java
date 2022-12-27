package com.ichinae.samples.service.impl;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ichinae.samples.bean.Permission;
import com.ichinae.samples.common.enums.GlobalDefaultStatusEnum;
import com.ichinae.samples.common.enums.SystemExceptionEnum;
import com.ichinae.samples.common.enums.UpmsExceptionEnum;
import com.ichinae.samples.common.exception.SystemException;
import com.ichinae.samples.enums.PermissionTypeEnum;
import com.ichinae.samples.mapper.PermissionMapper;
import com.ichinae.samples.mapper.RoleMapper;
import com.ichinae.samples.service.PermissionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author fuchengwei
 * @date 2020/12/22 8:42 下午
 */
@Service
@Transactional(rollbackFor = Exception.class)
@AllArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    private final PermissionMapper permissionMapper;
    private final RoleMapper roleMapper;

    @Override
    public Boolean isExists(String keyword, Integer permissionId) {
        // 字段验证
        Validator.validateNotEmpty(keyword, "关键字不能为空");

        Example example = new Example(Permission.class);
        example
                .createCriteria()
                .orEqualTo("permissionName", keyword)
                .orEqualTo("permissionValue", keyword);
        if (ObjectUtil.isNotNull(permissionId)) {
            example.and(example
                    .createCriteria()
                    .andNotEqualTo("permissionId", permissionId));
        }
        return ObjectUtil.isNotNull(getPermissionByExample(example));
    }

    @Override
    public Permission getPermissionByExample(Example example) {
        return permissionMapper.selectOneByExample(example);
    }

    @Override
    public List<Permission> getPermissionsByExample(Example example, boolean isRecursive) {
        // 通过排序字段升序排序
        example.setOrderByClause("sort ASC");
        example
                .createCriteria()
                .andEqualTo("permissionStatus", 0)
                .andEqualTo("display", 0);

        if (isRecursive) {
            List<Permission> permissions = permissionMapper.selectByExample(example);

            List<Integer> superiorIds = new ArrayList<>();
            superiorIds.add(0);

            List<Permission> newPermissions = recursionDataList(permissions, superiorIds);

            JSONArray jsonArray = treeRecursionDataList(newPermissions, 0);
            return JSONObject.parseArray(jsonArray.toJSONString(), Permission.class);
        } else {
            return permissionMapper.selectByExample(example);
        }
    }

    @Override
    public void updatePermissionSort(Integer[] permissionIds) {
        IntStream
                .range(0, permissionIds.length)
                .forEach(index -> {
                    Permission permission = new Permission()
                            .setPermissionId(permissionIds[index])
                            .setSort(index);
                    // 存入数据库
                    int result = permissionMapper.updateByPrimaryKeySelective(permission);
                    if (result == 0) {
                        throw new SystemException(SystemExceptionEnum.MYSQL_UPDATE_EXCEPTION);
                    }
                });
    }

    @Override
    public void insertPermission(Permission permission) {
        // 字段验证
        validation(permission, null);

        // 存入数据库
        int result = permissionMapper.insertSelective(permission);
        if (result == 0) {
            throw new SystemException(SystemExceptionEnum.MYSQL_INSERT_EXCEPTION);
        }
    }

    @Override
    public void updatePermission(Permission permission) {
        // 字段验证
        Validator.validateNotEmpty(permission.getPermissionId(), "权限id不能为空");
        validation(permission, permission.getPermissionId());

        permission.setUpdateTime(null);

        // 存入数据库
        int result = permissionMapper.updateByPrimaryKeySelective(permission);
        if (result == 0) {
            throw new SystemException(SystemExceptionEnum.MYSQL_UPDATE_EXCEPTION);
        }
    }

    @Override
    public void deletePermissions(Integer[] permissionIds) {
        // 字段验证
        Validator.validateNotEmpty(permissionIds, "权限Id不能为空");

        for (Integer permissionId : permissionIds) {
            int result = permissionMapper.deleteByPrimaryKey(permissionId);
            if (result == 0) {
                throw new SystemException(SystemExceptionEnum.MYSQL_DELETE_EXCEPTION);
            } else {
                roleMapper.deleteRolePermission(null, permissionId);
            }
        }
    }

    private JSONArray treeRecursionDataList(List<Permission> permissions, int superiorId) {
        JSONArray jsonArray = new JSONArray();

        permissions.forEach(permission -> {
            JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(permission));
            int permissionId = jsonObject.getIntValue("permissionId");
            int sId = jsonObject.getIntValue("superiorId");
            if (superiorId == sId) {
                JSONArray children = treeRecursionDataList(permissions, permissionId);
                jsonObject.put("children", children);
                jsonArray.add(jsonObject);
            }
        });

        return jsonArray;
    }

    private List<Permission> recursionDataList(List<Permission> permissions, List<Integer> superiorIds) {
        Map<Integer, Permission> map = new HashMap<>(0);

        permissions.forEach(permission -> {
            map.put(permission.getPermissionId(), permission);

            permission.setPermissionTypeName(PermissionTypeEnum
                    .getPermissionTypeEnumByCode(permission.getPermissionType())
                    .getDescribe());
            permission.setPermissionStatusName(GlobalDefaultStatusEnum.getDescribeByCode(permission.getPermissionStatus()));

            if (!superiorIds.contains(permission.getSuperiorId())) {
                superiorIds.add(permission.getSuperiorId());

                Example example = new Example(Permission.class);
                example
                        .createCriteria()
                        .andLike("permissionId", String.valueOf(permission.getSuperiorId()));
                recursionDataList(permissionMapper.selectByExample(example), superiorIds).forEach(newPermission -> map.put(newPermission.getPermissionId(), newPermission));
            }
        });

        return map
                .values()
                .stream()
                .sorted(Comparator.comparing(Permission::getSort))
                .collect(Collectors.toList());
    }

    public void validation(Permission permission, Integer permissionId) {
        // 字段验证
        Validator.validateNotEmpty(permission.getPermissionName(), "权限名称不能为空");
        Validator.validateNotEmpty(permission.getPermissionType(), "权限类型不能为空");

        if (permission
                .getPermissionType()
                .equals(PermissionTypeEnum.TYPE_BUTTON.getCode())) {
            Validator.validateNotEmpty(permission.getPermissionValue(), "权限值不能为空");
            if (isExists(permission.getPermissionValue(), permissionId)) {
                throw new SystemException(UpmsExceptionEnum.PERMISSION_INFO_EXCEPTION);
            }
        }
    }
}
