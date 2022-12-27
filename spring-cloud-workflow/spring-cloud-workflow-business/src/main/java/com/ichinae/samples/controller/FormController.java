package com.ichinae.samples.controller;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ichinae.samples.bean.Form;
import com.ichinae.samples.serivce.FormService;
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
 * @date 2021/4/21 4:28 下午
 * @description 表单模块
 */
@Api(tags = "表单模块")
@RestController
@RequestMapping("/forms")
@AllArgsConstructor
public class FormController {
    private final FormService formService;

    /**
     * 根据表单名称查询表单列表
     *
     * @param formName 表单名称
     * @param pageNum  当前页数
     * @param pageSize 每页条数
     * @return PageInfo<Form>
     */
    @ApiOperation("根据表单名称查询表单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "formName", value = "表单名称"),
            @ApiImplicitParam(name = "pageNum", value = "当前页数", defaultValue = "1"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", defaultValue = "10"),
    })
    @GetMapping("/getFormsByFormName")
    public PageInfo<Form> getFormsByFormName(@RequestParam(value = "formName", defaultValue = "", required = false) String formName,
                                             @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                             @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Form> forms = formService.getFormsByFormName(formName);
        return new PageInfo<>(forms);
    }

    /**
     * 根据表单名称查询是否已被注册
     * 传入表单Id的话就忽略该条表单Id
     *
     * @param formName 表单名称
     * @param formId   表单Id
     * @return true: 存在 false: 不存在
     */
    @ApiOperation("根据表单名称查询是否已被注册")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "formName", value = "表单名称", required = true),
            @ApiImplicitParam(name = "formId", value = "表单Id")
    })
    @GetMapping("/getIsExistsByFormName")
    public Boolean getIsExistsByFormName(String formName, Integer formId) {
        // 字段验证
        Validator.validateNotEmpty(formName, "表单名称不能为空");

        Example example = new Example(Form.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.orEqualTo("formName", formName);
        if (ObjectUtil.isNotNull(formId)) {
            example.and(example
                    .createCriteria()
                    .andNotEqualTo("formId", formId));
        }
        return formService.isExists(example);
    }

    /**
     * 获取所有表单列表
     *
     * @return List<Form>
     */
    @ApiOperation("获取所有表单列表")
    @GetMapping
    public List<Form> getForms() {
        return formService.getForms();
    }

    /**
     * 添加表单
     *
     * @param form 表单对象
     * @return string
     */
    @ApiOperation("添加表单")
    @PostMapping
    public String insertForm(Form form) {
        formService.insertForm(form);
        return "添加成功";
    }

    /**
     * 更新表单
     *
     * @param form 表单对象
     * @return string
     */
    @ApiOperation("更新表单")
    @PutMapping
    public String updateForm(Form form) {
        formService.updateForm(form);
        return "更新成功";
    }

    /**
     * 删除表单
     *
     * @param formIds 表单Ids
     * @return string
     */
    @ApiOperation("删除表单")
    @ApiImplicitParam(name = "formIds", value = "表单Id数组", required = true)
    @DeleteMapping
    public String deleteForms(Integer[] formIds) {
        // 字段验证
        Validator.validateNotEmpty(formIds, "表单Id不能为空");

        for (Integer formId : formIds) {
            formService.deleteForm(formId);
        }
        return "删除成功";
    }
}
