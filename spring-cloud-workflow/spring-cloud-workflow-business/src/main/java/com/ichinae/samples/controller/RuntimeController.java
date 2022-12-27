package com.ichinae.samples.controller;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.github.pagehelper.PageInfo;
import com.ichinae.samples.bean.Business;
import com.ichinae.samples.bean.FlowInfo;
import com.ichinae.samples.bean.User;
import com.ichinae.samples.common.enums.WorkflowExceptionEnum;
import com.ichinae.samples.common.exception.SystemException;
import com.ichinae.samples.common.utils.BeanUtil;
import com.ichinae.samples.common.vo.ServerResponse;
import com.ichinae.samples.serivce.BusinessService;
import com.ichinae.samples.serivce.FlowInfoService;
import com.ichinae.samples.serivce.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.ProcessInstanceQueryProperty;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static com.ichinae.samples.common.constants.Constants.USER_INFO;

/**
 * @author fuchengwei
 * @date 2021/4/27 10:06 上午
 * @description 运行管理模块
 */
@Api(tags = "运行管理模块")
@RestController
@RequestMapping("/runtime")
@AllArgsConstructor
public class RuntimeController {
    private final RepositoryService repositoryService;

    private final RuntimeService runtimeService;

    private final TaskService taskService;

    private final HistoryService historyService;

    private final BusinessService businessService;

    private final UserService userService;

    private final FlowInfoService flowInfoService;

    /**
     * 根据条件查询流程实例列表
     *
     * @param instanceId 流程实例Id
     * @param userName   发起人姓名
     * @param pageNum    当前页数
     * @param pageSize   每页条数
     * @return PageInfo
     */
    @ApiOperation("根据条件查询流程定义列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "instanceId", value = "流程实例Id"),
            @ApiImplicitParam(name = "userName", value = "发起人姓名"),
            @ApiImplicitParam(name = "pageNum", value = "当前页数", defaultValue = "1"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", defaultValue = "10")
    })
    @GetMapping("/getProcessInstances")
    public PageInfo<Map<String, Object>> getProcessInstances(String instanceId, String userName,
                                                             @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                                             @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize) {
        ProcessInstanceQuery processInstanceQuery = runtimeService
                .createProcessInstanceQuery()
                .orderBy(new ProcessInstanceQueryProperty("RES.START_TIME_"))
                .desc();

        if (StrUtil.isNotEmpty(instanceId)) {
            processInstanceQuery.processInstanceId(instanceId);
        }

        if (StrUtil.isNotEmpty(userName)) {
            User user = userService
                    .getUserByUserName(userName)
                    .getData();
            if (ObjectUtil.isNotNull(user)) {
                processInstanceQuery.startedBy(user
                        .getUserId()
                        .toString());
            } else {
                return new PageInfo<>();
            }
        }

        List<Map<String, Object>> maps = processInstanceQuery
                .listPage((pageNum - 1) * pageSize, pageSize)
                .stream()
                .map(processInstance -> {
                    Map<String, Object> map = new HashMap<>(4);

                    FlowInfo flowInfo = flowInfoService.getFlowInfoByFlowKey(processInstance.getProcessDefinitionKey());
                    User initiator = userService
                            .getUserByUserId(Integer.parseInt(processInstance.getStartUserId()))
                            .getData();
                    Business business = businessService.getBusinessByKey(processInstance.getBusinessKey());
                    Task task = taskService
                            .createTaskQuery()
                            .processInstanceId(processInstance.getProcessInstanceId())
                            .singleResult();

                    map.put("flowInfo", flowInfo);
                    map.put("initiator", initiator);
                    map.put("processInstance", BeanUtil.beanToMap(processInstance));
                    map.put("task", BeanUtil.beanToMap(task));
                    map.put("business", business);

                    if (StrUtil.isNotEmpty(task.getAssignee())) {
                        User transactor = userService
                                .getUserByUserId(Integer.parseInt(task.getAssignee()))
                                .getData();
                        map.put("transactor", transactor);
                    }

                    return map;
                })
                .collect(Collectors.toList());

        long total = processInstanceQuery.count();

        PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(maps);
        pageInfo.setTotal(total);

        return pageInfo;
    }

    /**
     * 获取流程图
     *
     * @param processInstanceId 流程实例Id
     * @return String
     */
    @ApiOperation("获取流程图")
    @ApiImplicitParam(name = "processInstanceId", value = "流程实例Id", required = true)
    @GetMapping("/getProcessDiagram")
    public String getProcessDiagram(String processInstanceId) throws IOException {
        // 字段验证
        Validator.validateNotEmpty(processInstanceId, "流程实例Id不能为空");

        ProcessInstance processInstance = runtimeService
                .createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();

        String processDefinitionId;
        List<String> activeActivityIds;

        if (ObjectUtil.isNotNull(processInstance)) {
            processDefinitionId = processInstance.getProcessDefinitionId();

            activeActivityIds = runtimeService.getActiveActivityIds(processInstanceId);
        } else {
            processDefinitionId = historyService
                    .createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult()
                    .getProcessDefinitionId();

            activeActivityIds = historyService
                    .createHistoricActivityInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .orderByHistoricActivityInstanceId()
                    .asc()
                    .list()
                    .stream()
                    .map(HistoricActivityInstance::getActivityId)
                    .collect(Collectors.toList());
        }

        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);

        DefaultProcessDiagramGenerator defaultProcessDiagramGenerator = new DefaultProcessDiagramGenerator();

        InputStream inputStream = defaultProcessDiagramGenerator.generateDiagram(bpmnModel, activeActivityIds);

        return StrUtil.format("data:image/svg+xml;base64,{}", Base64
                .getEncoder()
                .encodeToString(IOUtils.toByteArray(inputStream)));
    }

    /**
     * 获取流程节点信息
     *
     * @param processInstanceId 流程实例Id
     * @return Map
     */
    @ApiOperation("获取流程节点信息")
    @ApiImplicitParam(value = "processInstanceId", name = "流程实例Id", required = true)
    @GetMapping("/getProcessNodeInfo")
    public List<Map<String, Object>> getProcessNodeInfo(String processInstanceId) {
        final String startEvent = "startEvent";

        // 字段验证
        Validator.validateNotEmpty(processInstanceId, "流程实例Id不能为空");

        List<HistoricActivityInstance> historicActivityInstances = historyService
                .createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByHistoricActivityInstanceStartTime()
                .asc()
                .list();

        for (int i = 0; i < historicActivityInstances.size(); i++) {
            if (startEvent.equals(historicActivityInstances
                    .get(i)
                    .getActivityType())) {
                HistoricActivityInstance historicActivityInstance = historicActivityInstances.get(i);
                historicActivityInstances.remove(i);
                historicActivityInstances.add(0, historicActivityInstance);
                break;
            }
        }

        return historicActivityInstances
                .stream()
                .map(historicActivityInstance -> {
                    Map<String, Object> map = new HashMap<>(1);
                    map.put("historicActivityInstance", BeanUtil.beanToMap(historicActivityInstance));
                    if (startEvent.equals(historicActivityInstance.getActivityType())) {
                        ProcessInstance processInstance = runtimeService
                                .createProcessInstanceQuery()
                                .processInstanceId(processInstanceId)
                                .singleResult();

                        String startUserId;

                        if (ObjectUtil.isNotNull(processInstance)) {
                            startUserId = processInstance.getStartUserId();
                        } else {
                            startUserId = historyService
                                    .createHistoricProcessInstanceQuery()
                                    .processInstanceId(processInstanceId)
                                    .singleResult()
                                    .getStartUserId();
                        }

                        User user = userService
                                .getUserByUserId(Integer.parseInt(startUserId))
                                .getData();
                        map.put("initiator", user);
                    }

                    if (StrUtil.isNotEmpty(historicActivityInstance.getTaskId())) {
                        List<LinkedHashMap<String, Object>> variables = historyService
                                .createHistoricVariableInstanceQuery()
                                .taskId(historicActivityInstance.getTaskId())
                                .list()
                                .stream()
                                .map(BeanUtil::beanToMap)
                                .collect(Collectors.toList());

                        map.put("variables", variables);
                    }

                    if (StrUtil.isNotEmpty(historicActivityInstance.getAssignee())) {
                        User user = userService
                                .getUserByUserId(Integer.parseInt(historicActivityInstance.getAssignee()))
                                .getData();
                        map.put("user", user);
                    }

                    return map;
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取流程发起人
     *
     * @param processInstanceId 流程实例Id
     * @return User
     */
    @ApiOperation("获取流程发起人")
    @ApiImplicitParam(value = "processInstanceId", name = "流程实例Id", required = true)
    @GetMapping("/getProcessInitiator")
    public ServerResponse<User> getProcessInitiator(String processInstanceId) {
        // 字段验证
        Validator.validateNotEmpty(processInstanceId, "流程实例Id不能为空");

        String startUserId = runtimeService
                .createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult()
                .getStartUserId();

        return userService.getUserByUserId(Integer.parseInt(startUserId));
    }

    /**
     * 启动流程实例
     *
     * @param key     流程key
     * @param content 表单内容
     * @param value   表单值
     * @return String
     */
    @ApiOperation("启动流程实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", value = "流程key"),
            @ApiImplicitParam(name = "content", value = "表单内容"),
            @ApiImplicitParam(name = "value", value = "表单值"),
    })
    @PostMapping("/startProcessInstance")
    public String startProcessInstance(HttpServletRequest request, String key, String content, String value) throws IOException {
        // 字段验证
        Validator.validateNotEmpty(key, "流程Key不能为空");
        Validator.validateNotEmpty(content, "表单内容不能为空");
        Validator.validateNotEmpty(key, "表单值不能为空");

        // 查询流程定义
        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionKey(key)
                .latestVersion()
                .singleResult();

        InputStream inputStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), processDefinition.getResourceName());

        // 获取BpmnXml
        String bpmnXml = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

        final String startEvent = "startEvent";

        final String endEvent = "endEvent";

        // 判断BpmnXml中是否包含开始结束节点
        if (!StrUtil.containsIgnoreCase(bpmnXml, startEvent) && !StrUtil.containsIgnoreCase(bpmnXml, endEvent)) {
            throw new SystemException(WorkflowExceptionEnum.PROCESS_DEFINITION_CONTENT_EXCEPTION);
        }

        // 添加业务信息
        Business business = new Business()
                .setContent(content)
                .setValue(value);

        businessService.insertBusiness(business);

        // 获取用户信息
        User user = (User) request.getAttribute(USER_INFO);

        // 设置流程发起人
        Authentication.setAuthenticatedUserId(user
                .getUserId()
                .toString());

        // 开启流程
        Map<String, Object> map = JSONUtil.parseObj(value);

        runtimeService.startProcessInstanceByKey(key, business
                .getBusinessId()
                .toString(), map);

        return "启动成功";
    }

    /**
     * 流程激活与挂起
     *
     * @param instanceId 实例Id
     * @return String
     */
    @ApiOperation("流程激活或挂起")
    @ApiImplicitParam(name = "instanceId", value = "实例Id")
    @PostMapping("/processActivateOrSuspend")
    public String processActivateOrSuspend(String instanceId) {
        // 字段验证
        Validator.validateNotEmpty(instanceId, "流程实例Id不能为空");

        ProcessInstance processInstance = runtimeService
                .createProcessInstanceQuery()
                .processInstanceId(instanceId)
                .singleResult();

        if (ObjectUtil.isNull(processInstance)) {
            throw new SystemException(WorkflowExceptionEnum.PROCESS_INSTANCE_EXCEPTION);
        }

        String result;

        if (processInstance.isSuspended()) {
            runtimeService.activateProcessInstanceById(processInstance.getId());
            result = "流程实例激活成功";
        } else {
            runtimeService.suspendProcessInstanceById(processInstance.getId());
            result = "流程实例挂起成功";
        }

        return result;
    }

    /**
     * 删除流程实例
     *
     * @param instanceId 实例Id
     * @return String
     */
    @ApiOperation("删除流程实例")
    @ApiImplicitParam(name = "instanceId", value = "实例Id")
    @DeleteMapping("/{instanceId}")
    public String deleteProcessInstance(@PathVariable String instanceId) {
        // 字段验证
        Validator.validateNotEmpty(instanceId, "流程实例Id不能为空");

        runtimeService.deleteProcessInstance(instanceId, null);

        return "关闭流程";
    }
}
