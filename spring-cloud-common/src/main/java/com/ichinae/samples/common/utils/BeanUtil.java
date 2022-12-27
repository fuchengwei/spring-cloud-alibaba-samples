package com.ichinae.samples.common.utils;

import cn.hutool.core.bean.copier.BeanCopier;
import cn.hutool.core.bean.copier.CopyOptions;

import java.util.LinkedHashMap;

/**
 * @author fuchengwei
 * @date 2021/4/27 4:27 下午
 */
public class BeanUtil {
    public static LinkedHashMap<String, Object> beanToMap(Object obj) {
        return BeanCopier
                .create(obj,
                        new LinkedHashMap<String, Object>(),
                        CopyOptions
                                .create()
                                .setIgnoreNullValue(true)
                                .setIgnoreError(true)
                                .setFieldNameEditor(key -> key)
                )
                .copy();
    }
}
