package com.ichinae.samples.serivce.impl;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.ichinae.samples.bean.Form;
import com.ichinae.samples.common.enums.SystemExceptionEnum;
import com.ichinae.samples.common.enums.WorkflowExceptionEnum;
import com.ichinae.samples.common.exception.SystemException;
import com.ichinae.samples.mapper.FormMapper;
import com.ichinae.samples.serivce.FormService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author fuchengwei
 * @date 2021/4/21 4:56 下午
 */
@Service
@Transactional(rollbackOn = Exception.class)
@AllArgsConstructor
public class FormServiceImpl implements FormService {
    private final FormMapper formMapper;

    @Override
    public List<Form> getFormsByFormName(String formName) {
        Example example = new Example(Form.class);
        example
                .createCriteria()
                .andLike("formName", StrUtil.format("%{}%", formName));
        return formMapper.selectByExample(example);
    }

    @Override
    public List<Form> getForms() {
        return formMapper.selectAll();
    }

    @Override
    public Boolean isExists(Example example) {
        return ObjectUtil.isNotNull(formMapper.selectOneByExample(example));
    }

    @Override
    public void insertForm(Form form) {
        // 字段验证
        Validator.validateNotEmpty(form.getFormName(), "表单名称不能为空");

        Example example = new Example(Form.class);
        example
                .createCriteria()
                .andEqualTo("formName", form.getFormName());

        // 验证表单名称是否重复
        if (isExists(example)) {
            throw new SystemException(WorkflowExceptionEnum.FORM_NAME_REGISTERED_EXCEPTION);
        }

        // 存入数据库
        int result = formMapper.insertSelective(form);
        if (result == 0) {
            throw new SystemException(SystemExceptionEnum.MYSQL_INSERT_EXCEPTION);
        }
    }

    @Override
    public void updateForm(Form form) {
        // 字段验证
        Validator.validateNotEmpty(form.getFormName(), "表单名称不能为空");
        Validator.validateNotEmpty(form.getFormId(), "表单Id不能为空");

        Example example = new Example(Form.class);
        example
                .createCriteria()
                .andEqualTo("formName", form.getFormName())
                .andNotEqualTo("formId", form.getFormId());

        // 验证表单名称是否重复
        if (isExists(example)) {
            throw new SystemException(WorkflowExceptionEnum.FORM_NAME_REGISTERED_EXCEPTION);
        }

        form.setUpdateTime(null);

        // 存入数据库
        int result = formMapper.updateByPrimaryKeySelective(form);
        if (result == 0) {
            throw new SystemException(SystemExceptionEnum.MYSQL_UPDATE_EXCEPTION);
        }
    }

    @Override
    public void deleteForm(Integer formId) {
        // 字段验证
        Validator.validateNotEmpty(formId, "表单Id不能为空");

        // 删除数据库
        int result = formMapper.deleteByPrimaryKey(formId);
        if (result == 0) {
            throw new SystemException((SystemExceptionEnum.MYSQL_DELETE_EXCEPTION));
        }
    }
}
