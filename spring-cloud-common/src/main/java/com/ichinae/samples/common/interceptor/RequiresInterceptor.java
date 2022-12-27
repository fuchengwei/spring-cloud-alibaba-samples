package com.ichinae.samples.common.interceptor;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.ichinae.samples.bean.User;
import com.ichinae.samples.common.annotation.RequiresPermissions;
import com.ichinae.samples.common.constants.Constants;
import com.ichinae.samples.common.enums.ResponseEnum;
import com.ichinae.samples.common.vo.ServerResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fuchengwei
 * @date 2020/12/18 8:03 下午
 * @description 权限拦截器
 */
@Slf4j
@Component
@AllArgsConstructor
public class RequiresInterceptor implements HandlerInterceptor {
    private final StringRedisTemplate stringRedisTemplate;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;

            // 获取方法上注解
            RequiresPermissions requiresPermissions = handlerMethod
                    .getMethod()
                    .getAnnotation(RequiresPermissions.class);

            if (ObjectUtil.isNull(requiresPermissions)) {
                // 方法上的注解为空，直接return
                return true;
            } else {
                // 方法上标记了注解，则判断角色权限
                // 获取RedisKey
                String redisKey = (String) request.getAttribute(Constants.USER_INFO);
                // 获取Redis中的用户信息
                String json = stringRedisTemplate
                        .opsForValue()
                        .get(redisKey);
                // 反序列化
                User user = JSONUtil.toBean(json, User.class);
                // 允许访问的权限
                String[] permissions = requiresPermissions.permissions();
                // 创建权限值集合
                List<String> permissionValueList = new ArrayList<>();
                // 获取当前登录角色信息
                user
                        .getRoles()
                        .forEach(role -> role
                                .getPermissions()
                                .forEach(permissionDAO -> permissionValueList.add(permissionDAO.getPermissionValue())));

                for (String permission : permissions) {
                    if (permissionValueList.contains(permission)) {
                        log.info("当前请求用户包含权限：{}", permission);
                        return true;
                    }
                }

                response.setContentType("application/json;charset=utf-8");
                // 状态码 403：无权限
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                PrintWriter writer = response.getWriter();
                writer.write(JSONUtil.toJsonPrettyStr(ServerResponse.createByErrorCodeMessage(ResponseEnum.NOT_PERMISSION.getCode(), ResponseEnum.NOT_PERMISSION.getMessage())));
                writer.flush();
                writer.close();
                return false;
            }
        }
        return true;
    }
}
