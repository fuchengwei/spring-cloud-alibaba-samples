package com.ichinae.samples.serivce;

import com.ichinae.samples.bean.Form;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author fuchengwei
 * @date 2021/4/21 4:54 下午
 */
public interface FormService {
    /**
     * 根据表单名称查询表单列表
     *
     * @param formName 表单名称
     * @return List
     */
    List<Form> getFormsByFormName(String formName);

    /**
     * 获取所有表单列表
     *
     * @return List
     */
    List<Form> getForms();

    /**
     * 根据条件判断表单是否存在
     *
     * @param example 条件
     * @return true: 存在 false: 不存在
     */
    Boolean isExists(Example example);

    /**
     * 添加表单
     *
     * @param form 表单信息
     */
    void insertForm(Form form);

    /**
     * 更新表单
     *
     * @param form 表单信息
     */
    void updateForm(Form form);

    /**
     * 删除表单
     *
     * @param formId 表单Id
     */
    void deleteForm(Integer formId);
}
