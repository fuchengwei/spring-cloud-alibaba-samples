package com.ichinae.samples.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author fuchengwei
 * @date 2020/12/16 11:06 上午
 */
@Getter
@AllArgsConstructor
public enum UpmsExceptionEnum {
    /**
     * 用户权限管理系统异常枚举
     */
    USER_ID_EXCEPTION(61001, "用户Id不存在"),
    USER_ACCOUNT_EXCEPTION(61002, "用户账号不存在"),
    PHONE_REGISTERED_EXCEPTION(61003, "手机号已被注册"),
    PHONE_EMAIL_REGISTERED_EXCEPTION(61004, "手机号已被注册"),
    USER_IS_DELETE_EXCEPTION(61005, "账户已被删除,请联系管理员"),
    USER_NO_LOGIN_EXCEPTION(61006, "账户已被禁止登录,请联系管理员"),
    DEPARTMENT_NAME_EXCEPTION(61007, "部门名称已存在，请重新输入"),
    ROLE_NAME_EXCEPTION(61008, "角色名称已存在，请重新输入"),
    PERMISSION_INFO_EXCEPTION(61009, "权限名称或权限值已存在，请重新输入");

    private final Integer code;
    private final String message;
}
