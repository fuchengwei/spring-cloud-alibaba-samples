package com.ichinae.samples.gateway.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author fuchengwei
 * @date 2020/12/20 11:00 下午
 */
@FeignClient(value = "spring-cloud-upms", path = "/users")
public interface UserService {
    /**
     * 根据用户Id查询用户基本信息
     *
     * @param id 用户Id
     * @return ServerResponse<User>
     */
    @GetMapping("/{id}")
    String getUserByUserId(@PathVariable Integer id);
}
