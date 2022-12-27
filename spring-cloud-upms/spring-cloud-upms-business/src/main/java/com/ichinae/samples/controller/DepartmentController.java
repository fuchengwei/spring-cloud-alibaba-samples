package com.ichinae.samples.controller;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import com.ichinae.samples.bean.Department;
import com.ichinae.samples.service.DepartmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author fuchengwei
 * @date 2020/12/29 7:50 下午
 */
@Api(tags = "部门管理模块")
@RestController
@RequestMapping("/departments")
@AllArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;

    /**
     * 根据部门名称查询部门是否存在
     *
     * @param departmentName 部门名称
     * @return true: 存在 false: 不存在
     */
    @ApiOperation("根据部门名称查询部门是否存在")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "departmentName", value = "部门名称", required = true),
            @ApiImplicitParam(name = "departmentId", value = "部门Id", required = true)
    })
    @GetMapping("/getIsExistsByDepartmentName")
    public Boolean getIsExistsByDepartmentName(String departmentName, String departmentId) {
        // 字段验证
        Validator.validateNotEmpty(departmentName, "部门名称不能为空");

        Example example = new Example(Department.class);
        example
                .createCriteria()
                .andEqualTo("departmentName", departmentName);
        if (StrUtil.isNotBlank(departmentId)) {
            example.and(example
                    .createCriteria()
                    .andNotEqualTo("departmentId", departmentId));
        }
        return departmentService.isExists(example);
    }

    /**
     * 根据部门名称查询部门列表
     *
     * @param departmentName 部门名称
     * @return List<Department>
     */
    @ApiOperation("根据部门名称查询部门列表")
    @ApiImplicitParam(name = "departmentName", value = "部门名称")
    @GetMapping("/getDepartmentsByDepartmentName")
    public List<Department> getDepartmentsByDepartmentName(@RequestParam(value = "departmentName", defaultValue = "", required = false) String departmentName) {
        Example example = new Example(Department.class);
        example
                .createCriteria()
                .andLike("departmentName", StrUtil.format("%{}%", departmentName));
        example.setOrderByClause("CONVERT(department_name USING gbk) DESC");
        return departmentService.getDepartmentsByExample(example);
    }

    /**
     * 获取所有部门列表
     *
     * @return List<Department>
     */
    @ApiOperation("获取所有部门列表")
    @GetMapping
    public List<Department> getDepartments() {
        Example example = new Example(Department.class);
        example.setOrderByClause("CONVERT(department_name USING gbk) DESC");
        return departmentService.getDepartmentsByExample(example);
    }

    /**
     * 更新部门排序
     *
     * @param departmentIds 需要更新的权限Ids
     * @return String
     */
    @ApiOperation("更新部门排序")
    @PutMapping("/updateDepartmentSort")
    public String updateDepartmentSort(String[] departmentIds) {
        departmentService.updateDepartmentSort(departmentIds);
        return "更新成功";
    }

    /**
     * 添加部门
     *
     * @param department 部门信息
     * @return String
     */
    @ApiOperation("添加部门")
    @PostMapping
    public String insertDepartment(Department department) {
        departmentService.insertDepartment(department);
        return "添加成功";
    }

    /**
     * 更新部门
     *
     * @param department 部门信息
     * @return String
     */
    @ApiOperation("更新部门")
    @PutMapping
    public String updateDepartment(Department department) {
        departmentService.updateDepartment(department);
        return "修改成功";
    }

    /**
     * 删除部门
     *
     * @param departmentIds 部门Id数组
     * @return String
     */
    @ApiOperation("删除部门")
    @ApiImplicitParam(name = "departmentIds", value = "部门Id数组", required = true)
    @DeleteMapping
    public String deleteDepartments(String[] departmentIds) {
        departmentService.deleteDepartment(departmentIds);
        return "删除成功";
    }
}
