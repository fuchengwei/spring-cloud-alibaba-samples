package com.ichinae.samples.controller;

import cn.hutool.core.bean.BeanUtil;
import com.ichinae.samples.bean.User;
import com.ichinae.samples.serivce.CommonProcessService;
import com.ichinae.samples.serivce.FlowInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.activiti.engine.RepositoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ichinae.samples.common.constants.Constants.USER_INFO;

/**
 * @author fuchengwei
 * @date 2021/4/26 3:40 下午
 */
@Api(tags = "常用流程模块")
@RestController
@RequestMapping("/commonProcesses")
@AllArgsConstructor
public class CommonProcessController {
    private final CommonProcessService commonProcessService;
    private final RepositoryService repositoryService;
    private final FlowInfoService flowInfoService;

    /**
     * 获取当前登录用户的常用流程
     *
     * @return List<CommonProcess>
     */
    @ApiOperation("获取当前登录用户的常用流程")
    @GetMapping
    public List<Map<String, Object>> getCommonProcesses(HttpServletRequest request) {
        User user = (User) request.getAttribute(USER_INFO);
        return commonProcessService
                .getCommonProcesses(user.getUserId())
                .stream()
                .map(commonProcess -> {
                    Map<String, Object> map = new HashMap<>(2);
                    map.put("flowInfo", flowInfoService.getFlowInfoByFlowKey(commonProcess.getFlowKey()));
                    map.put("processDefinition", BeanUtil.beanToMap(repositoryService
                            .createProcessDefinitionQuery()
                            .processDefinitionKey(commonProcess.getFlowKey())
                            .latestVersion()
                            .singleResult(), false, true));
                    return map;
                })
                .collect(Collectors.toList());
    }

    /**
     * 添加常用流程
     *
     * @param keys 流程keys
     * @return String
     */
    @ApiOperation("添加常用流程")
    @ApiImplicitParam(name = "keys", value = "流程keys")
    @PostMapping
    public String insertCommonProcess(HttpServletRequest request, String[] keys) {
        // 获取当前登录用户信息
        User user = (User) request.getAttribute(USER_INFO);
        commonProcessService.insertCommonProcess(user.getUserId(), keys);

        return "保存成功";
    }
}
