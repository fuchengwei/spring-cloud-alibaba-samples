package com.ichinae.samples.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author fuchengwei
 * @date 2021/1/14 11:14 上午
 */
@Getter
@AllArgsConstructor
public enum GlobalDefaultStatusEnum {
    /**
     * 全局状态枚举
     */
    ENABLE(0, "启用"),
    DISABLE(1, "禁用");

    private final Integer code;
    private final String describe;

    public static String getDescribeByCode(Integer code) {
        for (GlobalDefaultStatusEnum globalDefaultStatusEnum : GlobalDefaultStatusEnum.values()) {
            if (globalDefaultStatusEnum.code.equals(code)) {
                return globalDefaultStatusEnum.describe;
            }
        }
        return null;
    }
}
