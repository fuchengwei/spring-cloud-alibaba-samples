package com.ichinae.samples.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ichinae.samples.bean.FlowType;
import com.ichinae.samples.serivce.FlowInfoService;
import com.ichinae.samples.serivce.FlowTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.activiti.engine.RepositoryService;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author fuchengwei
 * @date 2021/4/22 2:37 下午
 * @description 流程类型模块
 */
@Api(tags = "流程类型模块")
@RestController
@RequestMapping("/flowTypes")
@AllArgsConstructor
public class FlowTypeController {
    private final FlowTypeService flowTypeService;
    private final FlowInfoService flowInfoService;
    private final RepositoryService repositoryService;

    /**
     * 根据类型名称查询类型列表
     *
     * @param typeName 类型名称
     * @param pageNum  当前页数
     * @param pageSize 每页条数
     * @return PageInfo<FlowType>
     */
    @ApiOperation("根据类型名称查询类型列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "typeName", value = "类型名称"),
            @ApiImplicitParam(name = "pageNum", value = "当前页数", defaultValue = "1"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", defaultValue = "10"),
    })
    @GetMapping("/getFlowTypesByTypeName")
    public PageInfo<FlowType> getFlowTypesByTypeName(@RequestParam(value = "typeName", defaultValue = "", required = false) String typeName,
                                                     @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                                     @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<FlowType> flowTypes = flowTypeService.getFlowTypesByTypeName(typeName);
        return new PageInfo<>(flowTypes);
    }

    /**
     * 根据类型名称查询是否已被注册
     * 传入类型Id的话就忽略该条类型Id
     *
     * @param typeName 类型名称
     * @param typeId   类型Id
     * @return true: 存在 false: 不存在
     */
    @ApiOperation("根据类型名称查询是否已被注册")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "typeName", value = "类型名称", required = true),
            @ApiImplicitParam(name = "typeId", value = "类型Id")
    })
    @GetMapping("/getIsExistsByTypeName")
    public Boolean getIsExistsByTypeName(String typeName, Integer typeId) {
        // 字段验证
        Validator.validateNotEmpty(typeName, "类型名称不能为空");

        Example example = new Example(FlowType.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.orEqualTo("typeName", typeName);
        if (ObjectUtil.isNotNull(typeId)) {
            example.and(example
                    .createCriteria()
                    .andNotEqualTo("typeId", typeId));
        }
        return flowTypeService.isExists(example);
    }

    /**
     * 获取所有类型列表(包括流程定义信息)
     *
     * @return List<Map < String, Object>>
     */
    @ApiOperation("获取所有类型列表(包括流程定义信息)")
    @GetMapping("/getFlowTypesAndProcessDefinitions")
    public List<Map<String, Object>> getFlowTypesAndProcessDefinitions() {
        return flowTypeService
                .getFlowTypes()
                .stream()
                .map(flowType -> {
                            List<Map<String, Object>> processDefinitions = flowInfoService
                                    .getFlowInfosByTypeId(flowType.getTypeId())
                                    .stream()
                                    .map(flowInfo -> {
                                        Map<String, Object> map = new HashMap<>(2);
                                        map.put("flowInfo", flowInfo);
                                        map.put("processDefinition", BeanUtil.beanToMap(repositoryService
                                                .createProcessDefinitionQuery()
                                                .processDefinitionKey(flowInfo.getFlowKey())
                                                .latestVersion()
                                                .singleResult(), false, true));

                                        return map;
                                    })
                                    .collect(Collectors.toList());

                            Map<String, Object> map = new HashMap<>(2);
                            map.put("flowType", flowType);
                            map.put("processDefinitions", processDefinitions);

                            return map;
                        }
                )
                .collect(Collectors.toList());
    }

    /**
     * 获取所有类型列表
     *
     * @return List<FlowType>
     */
    @ApiOperation("获取所有类型列表")
    @GetMapping
    public List<FlowType> getFlowTypes() {
        return flowTypeService.getFlowTypes();
    }

    /**
     * 添加流程类型
     *
     * @param flowType 类型对象
     * @return string
     */
    @ApiOperation("添加流程类型")
    @PostMapping
    public String insertFlowType(FlowType flowType) {
        flowTypeService.insertFlowType(flowType);
        return "添加成功";
    }

    /**
     * 更新流程类型
     *
     * @param flowType 类型对象
     * @return string
     */
    @ApiOperation("更新流程类型")
    @PutMapping
    public String updateFlowType(FlowType flowType) {
        flowTypeService.updateFlowType(flowType);
        return "更新成功";
    }

    /**
     * 删除流程类型
     *
     * @param typeIds 类型Ids
     * @return string
     */
    @ApiOperation("删除流程类型")
    @ApiImplicitParam(name = "typeIds", value = "类型Id数组", required = true)
    @DeleteMapping
    public String deleteFlowTypes(Integer[] typeIds) {
        // 字段验证
        Validator.validateNotEmpty(typeIds, "类型Id不能为空");

        for (Integer typeId : typeIds) {
            flowTypeService.deleteFlowType(typeId);
        }
        return "删除成功";
    }
}
