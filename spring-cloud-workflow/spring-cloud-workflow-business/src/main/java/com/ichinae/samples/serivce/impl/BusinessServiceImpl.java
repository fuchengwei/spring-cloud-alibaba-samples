package com.ichinae.samples.serivce.impl;

import cn.hutool.core.lang.Validator;
import com.ichinae.samples.bean.Business;
import com.ichinae.samples.common.enums.SystemExceptionEnum;
import com.ichinae.samples.common.exception.SystemException;
import com.ichinae.samples.mapper.BusinessMapper;
import com.ichinae.samples.serivce.BusinessService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * @author fuchengwei
 * @date 2021/4/27 10:02 上午
 */
@Service
@Transactional(rollbackOn = Exception.class)
@AllArgsConstructor
public class BusinessServiceImpl implements BusinessService {
    private final BusinessMapper businessMapper;

    @Override
    public Business getBusinessByKey(String businessId) {
        // 字段验证
        Validator.validateNotEmpty(businessId, "businessId不能为空");

        return businessMapper.selectByPrimaryKey(businessId);
    }

    @Override
    public void insertBusiness(Business business) {
        // 字段验证
        Validator.validateNotEmpty(business.getContent(), "表单内容不能为空");
        Validator.validateNotEmpty(business.getValue(), "表单值不能为空");

        int result = businessMapper.insertSelective(business);

        if (result == 0) {
            throw new SystemException(SystemExceptionEnum.MYSQL_INSERT_EXCEPTION);
        }
    }
}
