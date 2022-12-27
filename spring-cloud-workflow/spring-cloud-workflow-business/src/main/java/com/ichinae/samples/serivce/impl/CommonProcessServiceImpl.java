package com.ichinae.samples.serivce.impl;

import cn.hutool.core.lang.Validator;
import com.ichinae.samples.bean.CommonProcess;
import com.ichinae.samples.common.enums.SystemExceptionEnum;
import com.ichinae.samples.common.exception.SystemException;
import com.ichinae.samples.mapper.CommonProcessMapper;
import com.ichinae.samples.serivce.CommonProcessService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author fuchengwei
 * @date 2021/4/26 3:36 下午
 */
@Service
@Transactional(rollbackOn = Exception.class)
@AllArgsConstructor
public class CommonProcessServiceImpl implements CommonProcessService {
    private final CommonProcessMapper commonProcessMapper;

    @Override
    public List<CommonProcess> getCommonProcesses(Integer userId) {
        // 字段验证
        Validator.validateNotEmpty(userId, "用户Id不能为空");

        return commonProcessMapper.select(new CommonProcess().setUserId(userId));
    }

    @Override
    public void insertCommonProcess(Integer userId, String[] keys) {
        // 字段验证
        Validator.validateNotEmpty(userId, "用户Id不能为空");
        Validator.validateNotEmpty(keys, "流程keys不能为空");

        // 删除原有数据
        commonProcessMapper.delete(new CommonProcess().setUserId(userId));

        // 循环添加数据
        for (String key : keys) {
            int result = commonProcessMapper.insertSelective(new CommonProcess()
                    .setUserId(userId)
                    .setFlowKey(key));

            if (result == 0) {
                throw new SystemException(SystemExceptionEnum.MYSQL_INSERT_EXCEPTION);
            }
        }
    }
}
