package com.ichinae.samples.mapper;

import com.ichinae.samples.bean.Permission;
import org.apache.ibatis.annotations.CacheNamespace;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author fuchengwei
 * @date 2020/12/22 8:16 下午
 */
@CacheNamespace
public interface PermissionMapper extends Mapper<Permission> {
}
