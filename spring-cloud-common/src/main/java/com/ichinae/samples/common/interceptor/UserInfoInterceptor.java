package com.ichinae.samples.common.interceptor;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.ichinae.samples.bean.User;
import com.ichinae.samples.common.constants.Constants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author fuchengwei
 * @date 2020/12/19 5:59 下午
 * @description 用户信息拦截器
 */
@Slf4j
@Component
@AllArgsConstructor
public class UserInfoInterceptor implements HandlerInterceptor {
    private final StringRedisTemplate stringRedisTemplate;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 获取RedisKey
        String redisKey = request.getHeader(Constants.USER_INFO);
        if (StrUtil.isNotEmpty(redisKey)) {
            // 获取Redis中UserInfo
            String json = stringRedisTemplate
                    .opsForValue()
                    .get(redisKey);
            if (StrUtil.isNotEmpty(json)) {
                // 反序列化
                User user = JSONUtil.toBean(json, User.class);
                // 向下传递用户信息
                request.setAttribute(Constants.USER_INFO, user);
                log.info("当前请求用户Id：{}，用户名：{}", user.getUserId(), user.getUserName());
            }
        }
        return true;
    }
}
