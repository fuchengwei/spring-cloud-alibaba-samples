package com.ichinae.samples.controller;

import com.ichinae.samples.bean.FlowInfo;
import com.ichinae.samples.serivce.FlowInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author fuchengwei
 * @date 2021/4/25 6:47 下午
 */
@Api(tags = "流程信息模块")
@RestController
@RequestMapping("/flowInfos")
@AllArgsConstructor
public class FlowInfoController {
    private final FlowInfoService flowInfoService;

    /**
     * 更新流程信息
     *
     * @param flowInfo 流程信息
     * @return String
     */
    @ApiOperation("更新流程信息")
    @PutMapping()
    public String updateFlowInfo(FlowInfo flowInfo) {
        flowInfoService.updateFlowInfo(flowInfo);
        return "更新成功";
    }
}
