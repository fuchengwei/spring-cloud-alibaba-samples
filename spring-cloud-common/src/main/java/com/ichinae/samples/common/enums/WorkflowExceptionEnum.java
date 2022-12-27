package com.ichinae.samples.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author fuchengwei
 * @date 2021/4/21 5:50 下午
 */
@Getter
@AllArgsConstructor
public enum WorkflowExceptionEnum {
    /**
     * 工作流系统异常枚举
     */
    FORM_NAME_REGISTERED_EXCEPTION(62001, "表单名称已被注册"),
    TYPE_NAME_REGISTERED_EXCEPTION(62002, "类型名称已被注册"),
    PROCESS_DEFINITION_EXCEPTION(62003, "流程定义不存在"),
    PROCESS_DEFINITION_CONTENT_EXCEPTION(62004, "流程定义内容为空"),
    PROCESS_INSTANCE_EXCEPTION(62005, "流程实例不存在");

    private final Integer code;
    private final String message;
}
