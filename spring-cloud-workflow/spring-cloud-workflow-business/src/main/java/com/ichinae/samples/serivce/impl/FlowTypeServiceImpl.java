package com.ichinae.samples.serivce.impl;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.ichinae.samples.bean.FlowType;
import com.ichinae.samples.common.enums.SystemExceptionEnum;
import com.ichinae.samples.common.enums.WorkflowExceptionEnum;
import com.ichinae.samples.common.exception.SystemException;
import com.ichinae.samples.mapper.FlowTypeMapper;
import com.ichinae.samples.serivce.FlowTypeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author fuchengwei
 * @date 2021/4/22 2:33 下午
 */
@Service
@Transactional(rollbackOn = Exception.class)
@AllArgsConstructor
public class FlowTypeServiceImpl implements FlowTypeService {
    private final FlowTypeMapper flowTypeMapper;

    @Override
    public List<FlowType> getFlowTypesByTypeName(String formName) {
        Example example = new Example(FlowType.class);
        example
                .createCriteria()
                .andLike("typeName", StrUtil.format("%{}%", formName));
        return flowTypeMapper.selectByExample(example);
    }

    @Override
    public List<FlowType> getFlowTypes() {
        return flowTypeMapper.selectAll();
    }

    @Override
    public Boolean isExists(Example example) {
        return ObjectUtil.isNotNull(flowTypeMapper.selectOneByExample(example));
    }

    @Override
    public void insertFlowType(FlowType flowType) {
        // 字段验证
        Validator.validateNotEmpty(flowType.getTypeName(), "类型名称不能为空");

        Example example = new Example(FlowType.class);
        example
                .createCriteria()
                .andEqualTo("typeName", flowType.getTypeName());

        // 验证表单名称是否重复
        if (isExists(example)) {
            throw new SystemException(WorkflowExceptionEnum.TYPE_NAME_REGISTERED_EXCEPTION);
        }

        // 存入数据库
        int result = flowTypeMapper.insertSelective(flowType);
        if (result == 0) {
            throw new SystemException(SystemExceptionEnum.MYSQL_INSERT_EXCEPTION);
        }
    }

    @Override
    public void updateFlowType(FlowType flowType) {
        // 字段验证
        Validator.validateNotEmpty(flowType.getTypeName(), "类型名称不能为空");
        Validator.validateNotEmpty(flowType.getTypeId(), "类型Id不能为空");

        Example example = new Example(FlowType.class);
        example
                .createCriteria()
                .andEqualTo("typeName", flowType.getTypeName())
                .andNotEqualTo("typeId", flowType.getTypeId());

        // 验证表单名称是否重复
        if (isExists(example)) {
            throw new SystemException(WorkflowExceptionEnum.TYPE_NAME_REGISTERED_EXCEPTION);
        }

        flowType.setUpdateTime(null);

        // 存入数据库
        int result = flowTypeMapper.updateByPrimaryKeySelective(flowType);
        if (result == 0) {
            throw new SystemException(SystemExceptionEnum.MYSQL_UPDATE_EXCEPTION);
        }
    }

    @Override
    public void deleteFlowType(Integer typeId) {
        // 字段验证
        Validator.validateNotEmpty(typeId, "类型Id不能为空");

        // 删除数据库
        int result = flowTypeMapper.deleteByPrimaryKey(typeId);
        if (result == 0) {
            throw new SystemException((SystemExceptionEnum.MYSQL_DELETE_EXCEPTION));
        }
    }
}
