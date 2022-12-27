package com.ichinae.samples.mapper;

import com.ichinae.samples.bean.Department;
import org.apache.ibatis.annotations.CacheNamespace;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author fuchengwei
 * @date 2020/12/25 11:54 上午
 */
@CacheNamespace
public interface DepartmentMapper extends Mapper<Department> {
}
