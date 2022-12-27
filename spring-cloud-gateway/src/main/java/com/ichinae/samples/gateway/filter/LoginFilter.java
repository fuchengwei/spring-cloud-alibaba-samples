package com.ichinae.samples.gateway.filter;

import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ichinae.samples.bean.Auth;
import com.ichinae.samples.common.constants.AuthConstants;
import com.ichinae.samples.common.constants.Constants;
import com.ichinae.samples.common.constants.RedisKeyConstants;
import com.ichinae.samples.common.enums.GlobalDefaultDeleteEnum;
import com.ichinae.samples.common.enums.GlobalDefaultStatusEnum;
import com.ichinae.samples.common.enums.ResponseEnum;
import com.ichinae.samples.common.enums.UpmsExceptionEnum;
import com.ichinae.samples.common.utils.JwtUtil;
import com.ichinae.samples.common.vo.ServerResponse;
import com.ichinae.samples.gateway.config.FilterConfig;
import com.ichinae.samples.gateway.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author fuchengwei
 * @date 2020/12/21 6:44 下午
 * @description 登录验证拦截器
 */
@Slf4j
@Component
@AllArgsConstructor
public class LoginFilter implements GlobalFilter, Ordered {
    private final FilterConfig filterConfig;
    private final UserService userService;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        if (isMatchCondition(request)) {
            // 获取token
            List<String> authorization = request
                    .getHeaders()
                    .get("Authorization");
            if (ObjectUtil.isNotNull(authorization)) {
                String token = authorization.get(0);
                // 判断是否存在Token
                if (StrUtil.isNotEmpty(token)) {
                    token = StrUtil
                            .subAfter(token, "Bearer", false)
                            .trim();
                    Integer userId = JwtUtil.parseJwt(token, Integer.class);
                    // 判断token是否正确
                    if (ObjectUtil.isNotNull(userId)) {
                        // 获取RedisKey
                        String redisKey = StrFormatter.format(RedisKeyConstants.TOKEN, userId);
                        // 获取Redis中的Token
                        String json = stringRedisTemplate
                                .opsForValue()
                                .get(redisKey);

                        // 判断json不为空
                        if (StrUtil.isNotEmpty(json)) {
                            // 反序列化
                            Auth auth = JSONUtil.toBean(json, Auth.class);
                            // 判断与redis中token是否一致
                            if (auth
                                    .getAccessToken()
                                    .equals(token)) {
                                // 获取最新用户信息
                                String jsonStr = userService.getUserByUserId(userId);
                                String userStr = new JSONObject(jsonStr).getStr("data");
                                Integer userStatus = new JSONObject(jsonStr)
                                        .getJSONObject("data")
                                        .getInt("status");
                                Integer userIsDelete = new JSONObject(jsonStr)
                                        .getJSONObject("data")
                                        .getInt("isDelete");
                                if (userStatus.equals(GlobalDefaultStatusEnum.DISABLE.getCode())) {
                                    // 判断账号是否禁用
                                    return returnErrorInfo(response, HttpStatus.OK, ServerResponse.createByErrorCodeMessage(UpmsExceptionEnum.USER_NO_LOGIN_EXCEPTION.getCode(), UpmsExceptionEnum.USER_NO_LOGIN_EXCEPTION.getMessage()));
                                }
                                if (userIsDelete.equals(GlobalDefaultDeleteEnum.DELETED.getCode())) {
                                    // 判断账号是否删除
                                    return returnErrorInfo(response, HttpStatus.OK, ServerResponse.createByErrorCodeMessage(UpmsExceptionEnum.USER_IS_DELETE_EXCEPTION.getCode(), UpmsExceptionEnum.USER_IS_DELETE_EXCEPTION.getMessage()));
                                }

                                // 生成RedisKey
                                String key = StrFormatter.format(RedisKeyConstants.USER_ID, userId);
                                // 缓存当前登录用户
                                stringRedisTemplate
                                        .opsForValue()
                                        .set(key, userStr, AuthConstants.REFRESH_TOKEN_EXPIRATION_TIME, TimeUnit.MILLISECONDS);
                                // 传递用户信息
                                ServerHttpRequest serverHttpRequest = request
                                        .mutate()
                                        .header(Constants.USER_INFO, key)
                                        .build();
                                ServerWebExchange webExchange = exchange
                                        .mutate()
                                        .request(serverHttpRequest)
                                        .build();
                                return chain.filter(webExchange);
                            } else {
                                return returnErrorInfo(response, HttpStatus.UNAUTHORIZED, ServerResponse.createByErrorCodeMessage(ResponseEnum.REPEAT_LOGIN.getCode(), ResponseEnum.REPEAT_LOGIN.getMessage()));
                            }
                        } else {
                            return returnErrorInfo(response, HttpStatus.UNAUTHORIZED, ServerResponse.createByErrorCodeMessage(ResponseEnum.OVERDUE_LOGIN.getCode(), ResponseEnum.OVERDUE_LOGIN.getMessage()));
                        }
                    } else {
                        return returnErrorInfo(response, HttpStatus.UNAUTHORIZED, ServerResponse.createByErrorCodeMessage(ResponseEnum.OVERDUE_LOGIN.getCode(), ResponseEnum.OVERDUE_LOGIN.getMessage()));
                    }
                } else {
                    return returnErrorInfo(response, HttpStatus.UNAUTHORIZED, ServerResponse.createByErrorCodeMessage(ResponseEnum.NEED_LOGIN.getCode(), ResponseEnum.NEED_LOGIN.getMessage()));
                }
            } else {
                return returnErrorInfo(response, HttpStatus.UNAUTHORIZED, ServerResponse.createByErrorCodeMessage(ResponseEnum.NEED_LOGIN.getCode(), ResponseEnum.NEED_LOGIN.getMessage()));
            }
        }

        return chain.filter(exchange);
    }

    private Boolean isMatchCondition(ServerHttpRequest request) {
        final String options = "OPTIONS";

        // 判断是否预请求
        if (options.equals(request.getMethodValue())) {
            return false;
        }

        for (String ignoreUrl : filterConfig.getIgnoreUrl()) {
            // 判断是否包含忽略请求URL
            if (StrUtil.containsIgnoreCase(request
                    .getURI()
                    .getPath(), ignoreUrl)) {
                return false;
            }
        }
        return true;
    }

    private Mono<Void> returnErrorInfo(ServerHttpResponse response, HttpStatus httpStatus, ServerResponse<?> serverResponse) {
        byte[] bytes = JSONUtil
                .toJsonPrettyStr(serverResponse)
                .getBytes();
        DataBuffer dataBuffer = response
                .bufferFactory()
                .wrap(bytes);
        response.setStatusCode(httpStatus);
        response
                .getHeaders()
                .add("Content-Type", "application/json;charset=UTF-8");
        return response.writeWith(Mono.just(dataBuffer));
    }
}
