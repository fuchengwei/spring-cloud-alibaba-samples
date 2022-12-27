package com.ichinae.samples.mapper;

import com.ichinae.samples.bean.FlowInfo;
import org.apache.ibatis.annotations.CacheNamespaceRef;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author fuchengwei
 * @date 2021/4/23 5:05 下午
 */
@CacheNamespaceRef(FlowInfoMapper.class)
public interface FlowInfoMapper extends Mapper<FlowInfo> {
    /**
     * 根据流程Key查询流程信息
     *
     * @param flowKey 流程key
     * @return flowInfo
     */
    FlowInfo getFlowInfoByFlowKey(@Param("flowKey") String flowKey);

    /**
     * 根据流程类型Id查询流程信息
     *
     * @param typeId 类型Id
     * @return list
     */
    List<FlowInfo> getFlowInfosByTypeId(@Param("typeId") Integer typeId);
}
