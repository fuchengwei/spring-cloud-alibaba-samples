package com.github.samples.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ChengWei.Fu
 * @date 2022/12/16
 */
@Getter
@AllArgsConstructor
public enum SystemExceptionEnum {
    /**
     * 系统公共异常信息枚举
     */
    TOO_MANY_REQUEST(429, "请求过多，接口限流"),
    MYSQL_INSERT_EXCEPTION(10001, "数据库添加异常"),
    MYSQL_UPDATE_EXCEPTION(10002, "数据库修改异常"),
    MYSQL_DELETE_EXCEPTION(10003, "数据库删除异常"),
    TYPE_TRANSFORM_EXCEPTION(11001, "类型转换异常"),
    REQUEST_PARAMETER_EXCEPTION(11002, "请求参数异常");

    private final Integer code;
    private final String message;
}
