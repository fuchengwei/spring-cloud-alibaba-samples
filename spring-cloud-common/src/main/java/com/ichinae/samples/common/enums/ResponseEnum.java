package com.ichinae.samples.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author fuchengwei
 * @date 2020/12/16 11:00 上午
 */
@Getter
@AllArgsConstructor
public enum ResponseEnum {
    /**
     * 响应枚举
     */
    SUCCESS(0, "请求成功"),
    ERROR(1, "请求失败"),
    EXCEPTION(2, "系统异常"),
    NEED_LOGIN(3, "未登录,需要登录"),
    OVERDUE_LOGIN(4, "登录信息已过期"),
    REPEAT_LOGIN(5, "该账号已在其他地点登录"),
    NOT_PERMISSION(6, "无访问权限");

    private final Integer code;
    private final String message;
}
