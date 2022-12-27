package com.ichinae.samples.serivce.impl;

import cn.hutool.core.lang.Validator;
import com.ichinae.samples.bean.FlowInfo;
import com.ichinae.samples.common.enums.SystemExceptionEnum;
import com.ichinae.samples.common.exception.SystemException;
import com.ichinae.samples.mapper.FlowInfoMapper;
import com.ichinae.samples.serivce.FlowInfoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author fuchengwei
 * @date 2021/4/23 5:46 下午
 */
@Service
@Transactional(rollbackOn = Exception.class)
@AllArgsConstructor
public class FlowInfoServiceImpl implements FlowInfoService {
    private final FlowInfoMapper flowInfoMapper;

    @Override
    public FlowInfo getFlowInfoByFlowKey(String flowKey) {
        return flowInfoMapper.getFlowInfoByFlowKey(flowKey);
    }

    @Override
    public List<FlowInfo> getFlowInfosByTypeId(Integer typeId) {
        return flowInfoMapper.getFlowInfosByTypeId(typeId);
    }

    @Override
    public void insertFlowInfo(FlowInfo flowInfo) {
        // 字段验证
        Validator.validateNotEmpty(flowInfo.getFlowKey(), "流程Key不能为空");
        Validator.validateNotEmpty(flowInfo.getTypeId(), "类型Id不能为空");

        // 存入数据库
        int result = flowInfoMapper.insertSelective(flowInfo);
        if (result == 0) {
            throw new SystemException(SystemExceptionEnum.MYSQL_INSERT_EXCEPTION);
        }

    }

    @Override
    public void updateFlowInfo(FlowInfo flowInfo) {
        // 字段验证
        Validator.validateNotEmpty(flowInfo.getFlowKey(), "流程Key不能为空");

        // 存入数据库
        int result = flowInfoMapper.updateByPrimaryKeySelective(flowInfo);
        if (result == 0) {
            throw new SystemException(SystemExceptionEnum.MYSQL_UPDATE_EXCEPTION);
        }
    }

    @Override
    public void deleteFlowInfo(String flowKey) {
        // 字段验证
        Validator.validateNotEmpty(flowKey, "流程Key不能为空");

        int result = flowInfoMapper.deleteByPrimaryKey(flowKey);

        if (result == 0) {
            throw new SystemException(SystemExceptionEnum.MYSQL_DELETE_EXCEPTION);
        }
    }
}
