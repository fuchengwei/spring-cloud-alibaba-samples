package com.ichinae.samples.mapper;

import com.ichinae.samples.bean.CommonProcess;
import org.apache.ibatis.annotations.CacheNamespace;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author fuchengwei
 * @date 2021/4/26 3:15 下午
 */
@CacheNamespace
public interface CommonProcessMapper extends Mapper<CommonProcess> {
}
