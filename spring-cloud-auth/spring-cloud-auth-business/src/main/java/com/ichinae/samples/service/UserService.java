package com.ichinae.samples.service;

import com.ichinae.samples.bean.User;
import com.ichinae.samples.common.vo.ServerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author fuchengwei
 * @date 2020/12/30 1:50 下午
 */
@FeignClient(value = "spring-cloud-upms", path = "/users")
public interface UserService {
    /**
     * 根据用户账号获取用户信息
     *
     * @param account 账号、邮箱、手机号
     * @return ServerResponse<User>
     */
    @GetMapping("/getUserByAccount")
    ServerResponse<User> getUserByAccount(@RequestParam(value = "account") String account);

    /**
     * 根据用户Id查询用户信息
     *
     * @param id 用户Id
     * @return ServerResponse<User>
     */
    @GetMapping("/{id}")
    ServerResponse<User> getUserByUserId(@PathVariable Integer id);
}
