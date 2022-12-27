package com.ichinae.samples.service.impl;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ichinae.samples.bean.Department;
import com.ichinae.samples.bean.User;
import com.ichinae.samples.common.enums.SystemExceptionEnum;
import com.ichinae.samples.common.enums.UpmsExceptionEnum;
import com.ichinae.samples.common.exception.SystemException;
import com.ichinae.samples.mapper.DepartmentMapper;
import com.ichinae.samples.mapper.UserMapper;
import com.ichinae.samples.service.DepartmentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author fuchengwei
 * @date 2020/12/25 12:03 下午
 */
@Service
@Transactional(rollbackFor = Exception.class)
@AllArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentMapper departmentMapper;
    private final UserMapper userMapper;

    @Override
    public Boolean isExists(Example example) {
        return ObjectUtil.isNotNull(getDepartmentByExample(example));
    }

    @Override
    public Department getDepartmentByExample(Example example) {
        return departmentMapper.selectOneByExample(example);
    }

    @Override
    public List<Department> getDepartmentsByExample(Example example) {
        // 通过排序字段升序排序
        example.setOrderByClause("sort ASC");

        List<Department> departments = departmentMapper.selectByExample(example);

        List<String> superiorIds = new ArrayList<>();
        superiorIds.add("");

        List<Department> newDepartments = recursionDataList(departments, superiorIds);

        JSONArray jsonArray = treeRecursionDataList(newDepartments, "");
        return JSONObject.parseArray(jsonArray.toJSONString(), Department.class);
    }

    @Override
    public void insertDepartment(Department department) {
        // 字段验证
        Validator.validateNotEmpty(department.getDepartmentName(), "部门名称不能为空");
        Validator.validateNotEmpty(department.getDescription(), "部门描述不能为空");

        Example example = new Example(Department.class);
        example
                .createCriteria()
                .andEqualTo("departmentName", department.getDepartmentName());

        // 判断部门名称是否存在
        if (isExists(example)) {
            throw new SystemException(UpmsExceptionEnum.DEPARTMENT_NAME_EXCEPTION);
        }

        example.clear();
        example
                .createCriteria()
                .andEqualTo("superiorId", department.getSuperiorId());

        int departmentCount = departmentMapper.selectCountByExample(example);
        int numberSize = 10;

        boolean isExists;

        do {
            departmentCount++;

            // 判断 departmentCount 是否小于10
            if (departmentCount < numberSize) {
                department.setDepartmentId(department.getSuperiorId() + "0" + departmentCount);
            } else {
                department.setDepartmentId(department.getSuperiorId() + departmentCount);
            }

            isExists = ObjectUtil.isNotNull(departmentMapper.selectByPrimaryKey(department));
        } while (isExists);

        // 存入数据库
        int result = departmentMapper.insertSelective(department);
        if (result == 0) {
            throw new SystemException(SystemExceptionEnum.MYSQL_INSERT_EXCEPTION);
        }
    }

    @Override
    public void updateDepartment(Department department) {
        // 字段验证
        Validator.validateNotEmpty(department.getDepartmentId(), "部门id不能为空");

        Example example = new Example(Department.class);
        example
                .createCriteria()
                .andEqualTo("departmentName", department.getDepartmentName())
                .andNotEqualTo("departmentId", department.getDepartmentId());

        // 判断部门名称是否存在
        if (isExists(example)) {
            throw new SystemException(UpmsExceptionEnum.DEPARTMENT_NAME_EXCEPTION);
        }

        department.setUpdateTime(null);

        // 存入数据库
        int result = departmentMapper.updateByPrimaryKeySelective(department);
        if (result == 0) {
            throw new SystemException(SystemExceptionEnum.MYSQL_UPDATE_EXCEPTION);
        }
    }

    @Override
    public void deleteDepartment(String[] departmentIds) {
        for (String departmentId : departmentIds) {
            Example example = new Example(Department.class);
            example
                    .createCriteria()
                    .andLike("departmentId", StrUtil.format("{}%", departmentId));
            // 删除部门数据
            int result = departmentMapper.deleteByExample(example);
            if (result != 0) {
                Example userExample = new Example(User.class);
                example
                        .createCriteria()
                        .andLike("departmentId", StrUtil.format("{}%", departmentId));
                List<User> users = userMapper.selectByExample(userExample);
                // 将被删除用户部门清空
                users.forEach(user -> {
                    user.setDepartmentId("");
                    userMapper.updateByPrimaryKey(user);
                });
            } else {
                throw new SystemException(SystemExceptionEnum.MYSQL_DELETE_EXCEPTION);
            }
        }
    }

    @Override
    public void updateDepartmentSort(String[] departmentIds) {
        IntStream
                .range(0, departmentIds.length)
                .forEach(index -> {
                    Department department = new Department()
                            .setDepartmentId(departmentIds[index])
                            .setSort(index);
                    // 存入数据库
                    int result = departmentMapper.updateByPrimaryKeySelective(department);
                    if (result == 0) {
                        throw new SystemException(SystemExceptionEnum.MYSQL_UPDATE_EXCEPTION);
                    }
                });
    }

    private JSONArray treeRecursionDataList(List<Department> departments, String superiorId) {
        JSONArray jsonArray = new JSONArray();

        departments.forEach(department -> {
            JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(department));
            String departmentId = jsonObject.getString("departmentId");
            String sId = jsonObject.getString("superiorId");
            if (superiorId.equals(sId)) {
                JSONArray children = treeRecursionDataList(departments, departmentId);
                jsonObject.put("children", children);
                jsonArray.add(jsonObject);
            }
        });
        return jsonArray;
    }

    private List<Department> recursionDataList(List<Department> departments, List<String> superiorIds) {
        Map<String, Department> map = new HashMap<>(0);

        departments.forEach(department -> {
            map.put(department.getDepartmentId(), department);

            if (!superiorIds.contains(department.getSuperiorId())) {
                superiorIds.add(department.getSuperiorId());

                Example example = new Example(Department.class);
                example
                        .createCriteria()
                        .andLike("departmentId", department.getSuperiorId());

                recursionDataList(departmentMapper.selectByExample(example), superiorIds).forEach(newDepartments -> map.put(newDepartments.getDepartmentId(), newDepartments));
            }
        });

        return map
                .values()
                .stream()
                .sorted(Comparator.comparing(Department::getSort))
                .collect(Collectors.toList());
    }
}
