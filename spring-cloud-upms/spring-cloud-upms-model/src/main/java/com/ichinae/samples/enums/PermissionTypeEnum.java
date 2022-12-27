package com.ichinae.samples.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ChengWei.Fu
 * @date 2022/12/9
 */
@Getter
@AllArgsConstructor
public enum PermissionTypeEnum {
    /**
     * 权限类型枚举
     */
    TYPE_DIRECTORY(1, "目录"),
    TYPE_MENU(2, "菜单"),
    TYPE_BUTTON(3, "按钮");

    private final Integer code;

    private final String describe;

    private static final Map<Integer, PermissionTypeEnum> permissionTypeMap = new HashMap<>();

    static {
        Arrays
                .stream(PermissionTypeEnum.values())
                .forEach(permissionTypeEnum -> permissionTypeMap.put(permissionTypeEnum.code, permissionTypeEnum));
    }

    public static PermissionTypeEnum getPermissionTypeEnumByCode(Integer code) {
        return permissionTypeMap.get(code);
    }
}
