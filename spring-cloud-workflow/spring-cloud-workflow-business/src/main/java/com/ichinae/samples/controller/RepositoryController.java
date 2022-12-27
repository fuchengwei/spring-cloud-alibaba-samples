package com.ichinae.samples.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageInfo;
import com.ichinae.samples.bean.FlowInfo;
import com.ichinae.samples.common.enums.WorkflowExceptionEnum;
import com.ichinae.samples.common.exception.SystemException;
import com.ichinae.samples.serivce.FlowInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author fuchengwei
 * @date 2021/4/23 4:40 下午
 */
@Api(tags = "资源管理模块")
@RestController
@RequestMapping("/repository")
@AllArgsConstructor
public class RepositoryController {
    private static final String FILE_TYPE = ".bpmn";

    private final RepositoryService repositoryService;
    private final FlowInfoService flowInfoService;

    /**
     * 根据条件查询流程定义列表
     *
     * @param name     部署名称/资源名称
     * @param pageNum  当前页数
     * @param pageSize 每页条数
     * @return PageInfo
     */
    @ApiOperation("根据条件查询流程定义列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "部署名称/资源名称"),
            @ApiImplicitParam(name = "pageNum", value = "当前页数", defaultValue = "1"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", defaultValue = "10")
    })
    @GetMapping("/getProcessDefinitionsByName")
    public PageInfo<Map<String, Object>> getProcessDefinitionsByName(@RequestParam(value = "name", defaultValue = "", required = false) String name,
                                                                     @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                                                     @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize) {
        ProcessDefinitionQuery processDefinitionQuery = repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionNameLike(StrUtil.format("%{}%", name))
                .processDefinitionResourceNameLike(StrUtil.format("%{}%", name))
                .latestVersion()
                .orderByProcessDefinitionVersion()
                .desc();

        List<Map<String, Object>> maps = processDefinitionQuery
                .listPage((pageNum - 1) * pageSize, pageSize)
                .stream()
                .map(processDefinition -> {
                    FlowInfo flowInfo = flowInfoService.getFlowInfoByFlowKey(processDefinition.getKey());

                    Map<String, Object> map = new HashMap<>(2);
                    map.put("processDefinition", BeanUtil.beanToMap(processDefinition, false, true));
                    map.put("flowInfo", flowInfo);

                    return map;
                })
                .collect(Collectors.toList());

        long total = processDefinitionQuery.count();

        PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(maps);
        pageInfo.setTotal(total);

        return pageInfo;
    }

    /**
     * 根据流程定义Id查询Xml
     *
     * @param definitionId 流程定义Id
     * @return String
     */
    @ApiOperation("根据流程定义Id查询Xml")
    @ApiImplicitParam(name = "definitionId", value = "流程定义Id")
    @GetMapping("/getProcessDefinitionXmlByDefinitionId")
    public String getProcessDefinitionXmlByDefinitionId(String definitionId) throws IOException {
        // 字段验证
        Validator.validateNotEmpty(definitionId, "流程定义Id不能为空");

        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionId(definitionId)
                .singleResult();

        if (ObjectUtil.isNull(processDefinition)) {
            throw new SystemException(WorkflowExceptionEnum.PROCESS_DEFINITION_EXCEPTION);
        }

        InputStream inputStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), processDefinition.getResourceName());

        return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
    }

    /**
     * 流程部署
     *
     * @param name   部署名称
     * @param xml    部署内容
     * @param typeId 类型Id
     * @return string
     */
    @ApiOperation("流程部署")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "部署名称"),
            @ApiImplicitParam(name = "xml", value = "部署内容"),
            @ApiImplicitParam(name = "typeId", value = "类型Id")
    })
    @PostMapping("/processDeployment")
    public String processDeployment(String name, String xml, Integer typeId) {
        // 字段验证
        Validator.validateNotEmpty(name, "部署名称不能为空");
        Validator.validateNotEmpty(xml, "部署内容不能为空");
        Validator.validateNotEmpty(typeId, "类型Id不能为空");

        // 流程部署
        Deployment deployment = repositoryService
                .createDeployment()
                .addString(name + FILE_TYPE, xml)
                .name(name)
                .deploy();

        // 获取Key
        String key = repositoryService
                .createProcessDefinitionQuery()
                .deploymentId(deployment.getId())
                .singleResult()
                .getKey();

        FlowInfo flowInfo = flowInfoService.getFlowInfoByFlowKey(key);

        if (ObjectUtil.isNotNull(flowInfo)) {
            flowInfoService.updateFlowInfo(flowInfo.setTypeId(typeId));
        } else {
            flowInfoService.insertFlowInfo(new FlowInfo()
                    .setFlowKey(key)
                    .setTypeId(typeId));
        }

        return "部署成功";
    }

    /**
     * 删除流程
     *
     * @param key 流程key
     * @return String
     */
    @ApiOperation("删除流程")
    @ApiImplicitParam(name = "key", value = "流程key")
    @DeleteMapping("/deleteProcessDeployment")
    public String deleteProcessDeployment(String key) {
        // 字段验证
        Validator.validateNotEmpty(key, "流程KEY不能为空");

        repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionKey(key)
                .list()
                .forEach(processDefinition -> repositoryService.deleteDeployment(processDefinition.getDeploymentId(), true));

        flowInfoService.deleteFlowInfo(key);

        return "删除成功";
    }

    /**
     * 流程激活与挂起
     *
     * @param definitionId 定义Id
     * @return String
     */
    @ApiOperation("流程激活或挂起")
    @ApiImplicitParam(name = "definitionId", value = "定义Id")
    @PostMapping("/processActivateOrSuspend")
    public String processActivateOrSuspend(String definitionId) {
        // 字段验证
        Validator.validateNotEmpty(definitionId, "流程定义Id不能为空");

        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionId(definitionId)
                .singleResult();

        if (ObjectUtil.isNull(processDefinition)) {
            throw new SystemException(WorkflowExceptionEnum.PROCESS_DEFINITION_EXCEPTION);
        }

        String result;

        if (processDefinition.isSuspended()) {
            repositoryService.activateProcessDefinitionById(processDefinition.getId(), true, null);
            result = "流程定义激活成功";
        } else {
            repositoryService.suspendProcessDefinitionById(processDefinition.getId(), true, null);
            result = "流程定义挂起成功";
        }

        return result;
    }
}
