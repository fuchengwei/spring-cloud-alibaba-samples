package com.ichinae.samples.serivce;

import com.ichinae.samples.bean.FlowInfo;

import java.util.List;

/**
 * @author fuchengwei
 * @date 2021/4/23 5:44 下午
 */
public interface FlowInfoService {
    /**
     * 根据流程key获取流程信息
     *
     * @param flowKey 流程key
     * @return FlowInfo
     */
    FlowInfo getFlowInfoByFlowKey(String flowKey);

    /**
     * 根据流程类型Id查询流程信息集合
     *
     * @param typeId 类型Id
     * @return list
     */
    List<FlowInfo> getFlowInfosByTypeId(Integer typeId);

    /**
     * 添加流程信息
     *
     * @param flowInfo 流程信息对象
     */
    void insertFlowInfo(FlowInfo flowInfo);

    /**
     * 更新流程信息
     *
     * @param flowInfo 流程信息对象
     */
    void updateFlowInfo(FlowInfo flowInfo);

    /**
     * 删除流程信息
     *
     * @param flowKey 流程key
     */
    void deleteFlowInfo(String flowKey);
}
