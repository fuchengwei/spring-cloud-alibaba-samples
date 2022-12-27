package com.ichinae.samples.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author fuchengwei
 * @date 2020/12/16 10:56 上午
 */
@Getter
@AllArgsConstructor
public enum AuthExceptionEnum {
    /**
     * 认证系统异常枚举
     */
    USER_ACCOUNT_PASSWORD_EXCEPTION(50001, "账号或密码错误"),
    USER_ACCOUNT_NOT_FIND_EXCEPTION(50002, "账号不存在"),
    REFRESH_TOKEN_EXCEPTION(50003, "刷新Token异常");

    private final Integer code;
    private final String message;
}
