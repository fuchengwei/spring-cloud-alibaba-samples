package com.ichinae.samples.mapper;

import com.ichinae.samples.bean.FlowType;
import org.apache.ibatis.annotations.CacheNamespace;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author fuchengwei
 * @date 2021/4/22 2:26 下午
 */
@CacheNamespace
public interface FlowTypeMapper extends Mapper<FlowType> {
}
