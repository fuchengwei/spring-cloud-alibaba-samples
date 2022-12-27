package com.ichinae.samples.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author fuchengwei
 * @date 2021/1/14 11:14 上午
 */
@Getter
@AllArgsConstructor
public enum GlobalDefaultDeleteEnum {
    /**
     * 全局删除枚举
     */
    NOT_DELETED(0, "未删除"),
    DELETED(1, "已删除");

    private final Integer code;
    private final String describe;
}
