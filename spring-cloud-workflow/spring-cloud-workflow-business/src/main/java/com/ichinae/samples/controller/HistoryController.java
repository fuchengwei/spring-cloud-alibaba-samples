package com.ichinae.samples.controller;

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
import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ichinae.samples.common.constants.Constants.USER_INFO;

/**
 * @author fuchengwei
 * @date 2021/5/6 5:15 下午
 */
@Api(tags = "历史管理模块")
@RestController
@RequestMapping("/history")
@AllArgsConstructor
public class HistoryController {
    private final HistoryService historyService;

    private final BusinessService businessService;

    private final UserService userService;

    private final FlowInfoService flowInfoService;

    /**
     * 根据条件查询历史任务列表
     *
     * @param processDefinitionKey  流程定义Key
     * @param processDefinitionName 流程定义名称
     * @param pageNum               当前页数
     * @param pageSize              每页条数
     * @return PageInfo
     */
    @ApiOperation("根据条件查询历史任务列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processDefinitionKey", value = "流程定义Key"),
            @ApiImplicitParam(name = "processDefinitionName", value = "流程定义名称"),
            @ApiImplicitParam(name = "pageNum", value = "当前页数", defaultValue = "1"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", defaultValue = "10")
    })
    @GetMapping("/getHistoryTasks")
    public PageInfo<Map<String, Object>> getHistoryTasks(HttpServletRequest request,
                                                         @RequestParam(value = "processDefinitionKey", defaultValue = "", required = false) String processDefinitionKey,
                                                         @RequestParam(value = "processDefinitionName", defaultValue = "", required = false) String processDefinitionName,
                                                         @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                                         @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize) {
        HistoricTaskInstanceQuery historicTaskInstanceQuery = historyService
                .createHistoricTaskInstanceQuery()
                .taskAssignee(((User) request.getAttribute(USER_INFO))
                        .getUserId()
                        .toString())
                .processDefinitionNameLike(StrUtil.format("%{}%", processDefinitionName))
                .processDefinitionKeyLikeIgnoreCase(StrUtil.format("%{}%", processDefinitionKey))
                .finished()
                .orderByTaskCreateTime()
                .desc();

        List<Map<String, Object>> maps = historicTaskInstanceQuery
                .listPage((pageNum - 1) * pageSize, pageSize)
                .stream()
                .map(historicTaskInstance -> {
                    Map<String, Object> map = new HashMap<>(5);
                    HistoricProcessInstance historicProcessInstance = historyService
                            .createHistoricProcessInstanceQuery()
                            .processInstanceId(historicTaskInstance.getProcessInstanceId())
                            .singleResult();

                    FlowInfo flowInfo = flowInfoService.getFlowInfoByFlowKey(historicProcessInstance.getProcessDefinitionKey());
                    Business business = businessService.getBusinessByKey(historicProcessInstance.getBusinessKey());
                    User initiator = userService
                            .getUserByUserId(Integer.parseInt(historicProcessInstance.getStartUserId()))
                            .getData();

                    map.put("flowInfo", flowInfo);
                    map.put("initiator", initiator);
                    map.put("historicTaskInstance", BeanUtil.beanToMap(historicTaskInstance));
                    map.put("historicProcessInstance", BeanUtil.beanToMap(historicProcessInstance));
                    map.put("business", business);

                    return map;
                })
                .collect(Collectors.toList());

        long total = historicTaskInstanceQuery.count();

        PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(maps);
        pageInfo.setTotal(total);

        return pageInfo;
    }
}
