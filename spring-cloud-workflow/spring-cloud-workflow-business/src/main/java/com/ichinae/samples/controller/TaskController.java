package com.ichinae.samples.controller;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageInfo;
import com.ichinae.samples.bean.Business;
import com.ichinae.samples.bean.FlowInfo;
import com.ichinae.samples.bean.User;
import com.ichinae.samples.common.utils.BeanUtil;
import com.ichinae.samples.serivce.BusinessService;
import com.ichinae.samples.serivce.FlowInfoService;
import com.ichinae.samples.serivce.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.TaskQuery;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ichinae.samples.common.constants.Constants.USER_INFO;

/**
 * @author fuchengwei
 * @date 2021/4/28 10:54 上午
 */
@Api(tags = "任务管理模块")
@RestController
@RequestMapping("/tasks")
@AllArgsConstructor
public class TaskController {
    private final RuntimeService runtimeService;

    private final TaskService taskService;

    private final UserService userService;

    private final FlowInfoService flowInfoService;

    private final BusinessService businessService;

    /**
     * 查询登录用户任务列表
     *
     * @param processDefinitionKey  流程定义key
     * @param processDefinitionName 流程定义名称
     * @param initiator             发起人姓名
     * @param pageNum               当前页数
     * @param pageSize              每页条数
     * @return PageInfo
     */
    @ApiOperation("查询登录用户任务列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processDefinitionKey", value = "流程定义Key"),
            @ApiImplicitParam(name = "processDefinitionName", value = "流程定义名称"),
            @ApiImplicitParam(name = "initiator", value = "发起人"),
            @ApiImplicitParam(name = "pageNum", value = "当前页数", defaultValue = "1"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", defaultValue = "10")
    })
    @GetMapping("/getMineTasks")
    public PageInfo<Map<String, Object>> getMineTasks(HttpServletRequest request,
                                                      @RequestParam(value = "processDefinitionKey", defaultValue = "", required = false) String processDefinitionKey,
                                                      @RequestParam(value = "processDefinitionName", defaultValue = "", required = false) String processDefinitionName,
                                                      @RequestParam(value = "initiator", defaultValue = "", required = false) String initiator,
                                                      @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                                      @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize) {
        TaskQuery taskQuery = taskService
                .createTaskQuery()
                .taskAssignee(((User) request.getAttribute(USER_INFO))
                        .getUserId()
                        .toString())
                .processDefinitionKeyLikeIgnoreCase(StrUtil.format("%{}%", processDefinitionKey))
                .processDefinitionNameLike(StrUtil.format("%{}%", processDefinitionName))
                .active()
                .orderByTaskCreateTime()
                .desc();

        if (StrUtil.isNotEmpty(initiator)) {
            User user = userService
                    .getUserByUserName(initiator)
                    .getData();
            if (ObjectUtil.isNotNull(user)) {
                List<ProcessInstance> processInstances = runtimeService
                        .createProcessInstanceQuery()
                        .startedBy(user
                                .getUserId()
                                .toString())
                        .list();

                taskQuery.processInstanceIdIn(processInstances
                        .stream()
                        .map(Execution::getId)
                        .collect(Collectors.toList()));
            } else {
                return new PageInfo<>();
            }
        }

        return getTasks(pageNum, pageSize, taskQuery);
    }

    /**
     * 查询登录用户组任务列表
     *
     * @param processDefinitionKey  流程定义key
     * @param processDefinitionName 流程定义名称
     * @param pageNum               当前页数
     * @param pageSize              每页条数
     * @return PageInfo
     */
    @ApiOperation("查询登录用户组任务列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processDefinitionKey", value = "流程定义Key"),
            @ApiImplicitParam(name = "processDefinitionName", value = "流程定义名称"),
            @ApiImplicitParam(name = "pageNum", value = "当前页数", defaultValue = "1"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", defaultValue = "10")
    })
    @GetMapping("/getGroupTasks")
    public PageInfo<Map<String, Object>> getGroupTasks(HttpServletRequest request,
                                                       @RequestParam(value = "processDefinitionKey", defaultValue = "", required = false) String processDefinitionKey,
                                                       @RequestParam(value = "processDefinitionName", defaultValue = "", required = false) String processDefinitionName,
                                                       @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                                       @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize) {
        User user = (User) request.getAttribute(USER_INFO);

        TaskQuery taskQuery = taskService
                .createTaskQuery()
                .processDefinitionKeyLikeIgnoreCase(StrUtil.format("%{}%", processDefinitionKey))
                .processDefinitionNameLike(StrUtil.format("%{}%", processDefinitionName))
                .taskCandidateUser(user
                        .getUserId()
                        .toString())
                .taskCandidateGroupIn(user
                        .getRoles()
                        .stream()
                        .map(role -> role
                                .getRoleId()
                                .toString())
                        .collect(Collectors.toList()))
                .active()
                .orderByTaskCreateTime()
                .desc();

        return getTasks(pageNum, pageSize, taskQuery);
    }

    /**
     * 委托任务
     *
     * @param taskId 任务Id
     * @param userId 用户Id
     * @return String
     */
    @ApiOperation("委托任务")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "taskId", name = "任务Id", required = true),
            @ApiImplicitParam(value = "userId", name = "用户Id", required = true)
    })
    @PostMapping("/delegateTask")
    public String delegateTask(String taskId, Integer userId) {
        // 字段验证
        Validator.validateNotEmpty(taskId, "任务Id不能为空");
        Validator.validateNotEmpty(userId, "用户Id不能为空");

        taskService.setAssignee(taskId, userId.toString());

        return "委托成功";
    }

    /**
     * 完成任务
     *
     * @param taskId 任务Id
     * @param result 审批结果
     * @param desc   审批意见
     * @return String
     */
    @ApiOperation("完成任务")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "taskId", name = "任务Id"),
            @ApiImplicitParam(value = "result", name = "流程结果"),
            @ApiImplicitParam(value = "desc", name = "流程意见"),
    })
    @PostMapping("/completeTask")
    public String completeTask(String taskId, Boolean result,
                               @RequestParam(name = "desc", defaultValue = "", required = false) String desc) {
        // 字段验证
        Validator.validateNotEmpty(taskId, "任务Id不能为空");
        Validator.validateNotEmpty(result, "审批结果不能为空");

        Map<String, Object> variables = new HashMap<>(2);
        variables.put("result", result);
        variables.put("desc", desc);

        taskService.complete(taskId, variables, true);

        return "完成任务";
    }

    /**
     * 拾取任务
     *
     * @param taskId 任务Id
     * @return String
     */
    @ApiOperation("拾取任务")
    @ApiImplicitParam(value = "taskId", name = "任务Id", required = true)
    @PostMapping("/claimTask")
    public String claimTask(HttpServletRequest request, String taskId) {
        // 字段验证
        Validator.validateNotEmpty(taskId, "任务Id不能为空");

        taskService.claim(taskId, ((User) request.getAttribute(USER_INFO))
                .getUserId()
                .toString());

        return "拾取成功";
    }

    /**
     * 退还任务
     *
     * @param taskId 任务Id
     * @return String
     */
    @ApiOperation("退还任务")
    @ApiImplicitParam(value = "taskId", name = "任务Id", required = true)
    @PostMapping("/handBackTask")
    public String handBackTask(String taskId) {
        // 字段验证
        Validator.validateNotEmpty(taskId, "任务Id不能为空");

        taskService.setAssignee(taskId, null);

        return "退还成功";
    }

    private PageInfo<Map<String, Object>> getTasks(Integer pageNum, Integer pageSize, TaskQuery taskQuery) {
        List<Map<String, Object>> maps = taskQuery
                .listPage((pageNum - 1) * pageSize, pageSize)
                .stream()
                .map(task -> {
                    ProcessInstance processInstance = runtimeService
                            .createProcessInstanceQuery()
                            .processInstanceId(task.getProcessInstanceId())
                            .singleResult();

                    User initiator = userService
                            .getUserByUserId(Integer.parseInt(processInstance.getStartUserId()))
                            .getData();
                    FlowInfo flowInfo = flowInfoService.getFlowInfoByFlowKey(processInstance.getProcessDefinitionKey());
                    Business business = businessService.getBusinessByKey(processInstance.getBusinessKey());

                    Map<String, Object> map = new HashMap<>(5);
                    map.put("task", BeanUtil.beanToMap(task));
                    map.put("processInstance", BeanUtil.beanToMap(processInstance));
                    map.put("initiator", initiator);
                    map.put("flowInfo", flowInfo);
                    map.put("business", business);

                    return map;
                })
                .collect(Collectors.toList());

        long total = taskQuery.count();

        PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(maps);
        pageInfo.setTotal(total);

        return pageInfo;
    }
}
