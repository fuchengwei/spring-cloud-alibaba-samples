package com.ichinae.samples.mapper;

import com.ichinae.samples.bean.Form;
import org.apache.ibatis.annotations.CacheNamespace;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author fuchengwei
 * @date 2021/4/21 4:53 下午
 */
@CacheNamespace
public interface FormMapper extends Mapper<Form> {
}
