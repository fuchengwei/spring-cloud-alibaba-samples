package com.ichinae.samples.serivce;

import com.ichinae.samples.bean.FlowType;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author fuchengwei
 * @date 2021/4/22 2:28 下午
 */
public interface FlowTypeService {
    /**
     * 根据类型名称查询类型列表
     *
     * @param typeName 类型名称
     * @return List
     */
    List<FlowType> getFlowTypesByTypeName(String typeName);

    /**
     * 获取所有类型列表
     *
     * @return List<FlowType>
     */
    List<FlowType> getFlowTypes();

    /**
     * 根据条件判断流程类型是否存在
     *
     * @param example 条件
     * @return true: 存在 false: 不存在
     */
    Boolean isExists(Example example);

    /**
     * 添加类型
     *
     * @param flowType 类型信息
     */
    void insertFlowType(FlowType flowType);

    /**
     * 更新类型
     *
     * @param flowType 类型信息
     */
    void updateFlowType(FlowType flowType);

    /**
     * 删除类型
     *
     * @param typeId 类型Id
     */
    void deleteFlowType(Integer typeId);
}
