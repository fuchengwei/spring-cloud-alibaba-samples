package com.ichinae.samples.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ichinae.samples.bean.Role;
import com.ichinae.samples.service.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author fuchengwei
 * @date 2020/12/29 3:52 下午
 */
@Api(tags = "角色管理模块")
@RestController
@RequestMapping("/roles")
@AllArgsConstructor
public class RoleController {
    private final RoleService roleService;

    /**
     * 根据角色名称查询角色是否存在
     *
     * @param roleName 角色名称
     * @param roleId   角色Id
     * @return true：存在 false：不存在
     */
    @ApiOperation("根据关键字查询角色是否存在")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleName", value = "角色名称", required = true),
            @ApiImplicitParam(name = "roleId", value = "角色Id")
    })
    @GetMapping("/getIsExistsByRoleName")
    public Boolean getIsExistsByRoleName(String roleName, Integer roleId) {
        return roleService.isExists(roleName, roleId);
    }

    /**
     * 根据角色名称查询角色集合
     *
     * @param roleName 角色名称
     * @param pageNum  当前页数
     * @param pageSize 每页数量
     * @return PageInfo<Role>
     */
    @ApiOperation("根据角色名称查询角色集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleName", value = "角色名称"),
            @ApiImplicitParam(name = "pageNum", value = "当前页数", defaultValue = "1"),
            @ApiImplicitParam(name = "pageSize", value = "每页数量", defaultValue = "10")
    })
    @GetMapping("/getRolesByRoleName")
    public PageInfo<Role> getRolesByKeyword(@RequestParam(value = "roleName", defaultValue = "", required = false) String roleName,
                                            @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                            @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Role> roles = roleService.getRolesByRoleName(roleName);
        return new PageInfo<>(roles);
    }

    /**
     * 查询全部角色信息
     *
     * @return List<Role>
     */
    @ApiOperation("查询全部角色信息")
    @GetMapping
    public List<Role> getRoles() {
        return roleService.getRoles();
    }

    /**
     * 添加角色
     *
     * @param role 角色信息
     * @return String
     */
    @ApiOperation("添加角色")
    @PostMapping
    public String insertRole(Role role) {
        roleService.insertRole(role);
        return "添加成功";
    }

    /**
     * 更新角色
     *
     * @param role 角色信息
     * @return String
     */
    @ApiOperation("更新角色")
    @PutMapping
    public String updateRole(Role role) {
        roleService.updateRole(role);
        return "更新成功";
    }

    /**
     * 删除角色
     *
     * @param roleIds 角色Id数组
     * @return String
     */
    @ApiOperation("删除角色")
    @ApiImplicitParam(name = "roleIds", value = "角色Id数组", required = true)
    @DeleteMapping
    public String deleteRoles(Integer[] roleIds) {
        roleService.deleteRoles(roleIds);
        return "删除成功";
    }

    /**
     * 添加角色权限
     *
     * @param id            角色Id
     * @param permissionIds 权限Id数组
     * @return String
     */
    @ApiOperation("添加角色权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "角色Id", paramType = "path", required = true),
            @ApiImplicitParam(name = "permissionIds", value = "权限Id数组")
    })
    @PostMapping("/{id}/permissions")
    public String insertRolePermissions(@PathVariable Integer id, Integer[] permissionIds) {
        roleService.insertRolePermission(id, permissionIds);
        return "配置角色权限成功";
    }
}
