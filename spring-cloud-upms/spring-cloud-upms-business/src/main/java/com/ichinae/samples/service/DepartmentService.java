package com.ichinae.samples.service;

import com.ichinae.samples.bean.Department;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author fuchengwei
 * @date 2020/12/25 11:56 上午
 */
public interface DepartmentService {
    /**
     * 根据条件判断部门是否存在
     *
     * @param example 条件
     * @return true: 存在 false: 不存在
     */
    Boolean isExists(Example example);

    /**
     * 根据条件查询部门
     *
     * @param example 条件
     * @return 部门对象
     */
    Department getDepartmentByExample(Example example);

    /**
     * 根据条件查询部门列表表
     *
     * @param example 条件
     * @return list
     */
    List<Department> getDepartmentsByExample(Example example);

    /**
     * 添加部门
     *
     * @param department 部门信息
     */
    void insertDepartment(Department department);

    /**
     * 更新部门
     *
     * @param department 部门信息
     */
    void updateDepartment(Department department);

    /**
     * 删除部门
     *
     * @param departmentIds 部门Ids数组
     */
    void deleteDepartment(String[] departmentIds);

    /**
     * 更新部门排序
     *
     * @param departmentIds 部门Ids
     */
    void updateDepartmentSort(String[] departmentIds);
}
