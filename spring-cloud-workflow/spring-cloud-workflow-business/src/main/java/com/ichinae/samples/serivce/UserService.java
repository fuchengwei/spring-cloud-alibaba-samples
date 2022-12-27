package com.ichinae.samples.serivce;

import com.ichinae.samples.bean.User;
import com.ichinae.samples.common.vo.ServerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author fuchengwei
 * @date 2021/4/27 2:11 下午
 */
@FeignClient(value = "spring-cloud-upms", path = "/users")
public interface UserService {
    /**
     * 根据用户名称查询用户信息
     *
     * @param userName 用户名称
     * @return ServerResponse<User>
     */
    @GetMapping("/getUserByUserName")
    ServerResponse<User> getUserByUserName(@RequestParam(value = "userName") String userName);

    /**
     * 根据用户Id查询用户信息
     *
     * @param id 用户Id
     * @return ServerResponse<User>
     */
    @GetMapping("/{id}")
    ServerResponse<User> getUserByUserId(@PathVariable Integer id);
}
