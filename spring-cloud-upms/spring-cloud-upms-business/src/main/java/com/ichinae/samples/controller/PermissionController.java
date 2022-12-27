package com.ichinae.samples.controller;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import com.ichinae.samples.bean.Permission;
import com.ichinae.samples.service.PermissionService;
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
 * @date 2020/12/29 7:29 下午
 */
@Api(tags = "权限管理模块")
@RestController
@RequestMapping("/permissions")
@AllArgsConstructor
public class PermissionController {
    private final PermissionService permissionService;

    /**
     * 根据关键字查询权限是否存在
     *
     * @param keyword      权限名称、权限值
     * @param permissionId 权限Id
     * @return true：存在 false：不存在
     */
    @ApiOperation("根据关键字查询权限是否存在")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "权限名称、权限值", required = true),
            @ApiImplicitParam(name = "permissionId", value = "权限Id")
    })
    @GetMapping("/getIsExistsByKeyword")
    public Boolean getIsExistsByKeyword(String keyword, Integer permissionId) {
        return permissionService.isExists(keyword, permissionId);
    }

    /**
     * 根据关键字查询权限列表
     *
     * @param keyword 关键字
     * @return List<Permission>
     */
    @ApiOperation("根据关键字查询权限列表")
    @ApiImplicitParam(name = "keyword", value = "关键字")
    @GetMapping("/getPermissionsByKeyword")
    public List<Permission> getPermissionsByKeyword(@RequestParam(value = "keyword", defaultValue = "", required = false) String keyword) {
        Example example = new Example(Permission.class);
        example
                .createCriteria()
                .orLike("permissionName", StrUtil.format("%{}%", keyword))
                .orLike("permissionValue", StrUtil.format("%{}%", keyword))
                .orLike("permissionType", StrUtil.format("%{}%", keyword));
        return permissionService.getPermissionsByExample(example, true);
    }

    /**
     * 查询所有权限列表
     *
     * @return List<Permission>
     */
    @ApiOperation("查询所有权限列表")
    @GetMapping
    public List<Permission> getPermissions() {
        Example example = new Example(Permission.class);
        return permissionService.getPermissionsByExample(example, true);
    }

    /**
     * 根据类型查询权限信息
     *
     * @param permissionType 权限类型
     * @return List<Permission>
     */
    @ApiOperation("根据类型查询权限信息")
    @ApiImplicitParam(name = "permissionType", value = "权限类型", required = true)
    @GetMapping("/getPermissionsByType")
    public List<Permission> getPermissionsByType(Integer permissionType) {
        // 字段验证
        Validator.validateNotEmpty(permissionType, "权限类型不能为空");

        Example example = new Example(Permission.class);
        example
                .createCriteria()
                .andEqualTo("permissionType", permissionType);
        return permissionService.getPermissionsByExample(example, true);
    }

    /**
     * 更新权限排序
     *
     * @param permissionIds 需要更新的权限Ids
     * @return String
     */
    @ApiOperation("更新权限排序")
    @PutMapping("/updatePermissionSort")
    public String updatePermissionSort(Integer[] permissionIds) {
        permissionService.updatePermissionSort(permissionIds);
        return "更新成功";
    }

    /**
     * 添加权限
     *
     * @param permission 权限信息
     * @return String
     */
    @ApiOperation("添加权限")
    @PostMapping
    public String insertPermission(Permission permission) {
        permissionService.insertPermission(permission);
        return "添加成功";
    }

    /**
     * 修改权限
     *
     * @param permission 权限信息
     * @return String
     */
    @ApiOperation("修改权限")
    @PutMapping
    public String updatePermission(Permission permission) {
        permissionService.updatePermission(permission);
        return "修改成功";
    }

    /**
     * 删除权限
     *
     * @param permissionIds 权限Id数组
     * @return String
     */
    @ApiOperation("删除权限")
    @ApiImplicitParam(name = "permissionIds", value = "权限Id数组", required = true)
    @DeleteMapping
    public String deletePermissions(Integer[] permissionIds) {
        permissionService.deletePermissions(permissionIds);
        return "删除成功";
    }
}
