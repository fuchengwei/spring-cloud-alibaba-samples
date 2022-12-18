package com.github.samples.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ChengWei.Fu
 * @date 2022/12/18
 */
@Getter
@AllArgsConstructor
public enum GlobalDefaultStatusEnum {
    /**
     * 全局状态枚举
     */
    DISABLE(0, "禁用"),
    ENABLE(1, "启用");

    private final Integer code;
    private final String describe;

    private static final Map<Integer, GlobalDefaultStatusEnum> globalDefaultStatusMap = new HashMap<>();

    static {
        Arrays
                .stream(GlobalDefaultStatusEnum.values())
                .forEach(globalDefaultStatusEnum -> globalDefaultStatusMap.put(globalDefaultStatusEnum.code, globalDefaultStatusEnum));
    }

    public static GlobalDefaultStatusEnum getGlobalDefaultStatusEnumByCode(Integer code) {
        return globalDefaultStatusMap.get(code);
    }
}
