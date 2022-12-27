package com.ichinae.samples.common.annotation;

import java.lang.annotation.*;

/**
 * @author fuchengwei
 * @date 2020/12/18 7:27 下午
 * @description 权限认证注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresPermissions {
    /**
     * 权限数组
     */
    String[] permissions();
}
