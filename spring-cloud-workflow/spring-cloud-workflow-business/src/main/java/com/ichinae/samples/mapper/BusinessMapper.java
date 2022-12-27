package com.ichinae.samples.mapper;

import com.ichinae.samples.bean.Business;
import org.apache.ibatis.annotations.CacheNamespace;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author fuchengwei
 * @date 2021/4/27 9:58 上午
 */
@CacheNamespace
public interface BusinessMapper extends Mapper<Business> {
}
