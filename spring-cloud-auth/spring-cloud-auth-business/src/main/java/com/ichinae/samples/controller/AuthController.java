package com.ichinae.samples.controller;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.ichinae.samples.bean.Auth;
import com.ichinae.samples.bean.User;
import com.ichinae.samples.common.constants.AuthConstants;
import com.ichinae.samples.common.constants.RedisKeyConstants;
import com.ichinae.samples.common.enums.AuthExceptionEnum;
import com.ichinae.samples.common.enums.GlobalDefaultDeleteEnum;
import com.ichinae.samples.common.enums.GlobalDefaultStatusEnum;
import com.ichinae.samples.common.enums.UpmsExceptionEnum;
import com.ichinae.samples.common.exception.SystemException;
import com.ichinae.samples.common.utils.JwtUtil;
import com.ichinae.samples.common.vo.ServerResponse;
import com.ichinae.samples.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

/**
 * @author fuchengwei
 * @date 2020/12/18 4:57 下午
 */
@Api(tags = "认证授权模块")
@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final UserService userService;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 创建token
     *
     * @param account  账号、邮箱、手机号
     * @param password 密码
     * @return Auth
     */
    @ApiOperation("创建token")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "account", value = "账号、邮箱、手机号", required = true),
            @ApiImplicitParam(name = "password", value = "密码", required = true)
    })
    @PostMapping("/createToken")
    public Auth createToken(String account, String password) {
        ServerResponse<User> serverResponse = userService.getUserByAccount(account);
        User user = serverResponse.getData();
        // 判断账号是否存在
        if (ObjectUtil.isNull(user)) {
            throw new SystemException(AuthExceptionEnum.USER_ACCOUNT_NOT_FIND_EXCEPTION);
        }
        // 判断账号是否禁用
        if (user
                .getStatus()
                .equals(GlobalDefaultStatusEnum.DISABLE.getCode())) {
            throw new SystemException(UpmsExceptionEnum.USER_NO_LOGIN_EXCEPTION);
        }
        // 判断账号是否被删除
        if (user
                .getIsDelete()
                .equals(GlobalDefaultDeleteEnum.DELETED.getCode())) {
            throw new SystemException(UpmsExceptionEnum.USER_IS_DELETE_EXCEPTION);
        }
        // 判断密码是否正确
        if (!DigestUtil.bcryptCheck(password, user.getPassword())) {
            throw new SystemException(AuthExceptionEnum.USER_ACCOUNT_PASSWORD_EXCEPTION);
        }
        return createAuth(user.getUserId());
    }

    /**
     * 刷新token
     *
     * @param refreshToken 刷新token
     * @return Auth
     */
    @ApiOperation("刷新token")
    @ApiImplicitParam(name = "refreshToken", value = "刷新token", required = true)
    @PutMapping("/refreshToken")
    public Auth refreshToken(String refreshToken) {
        // 字段验证
        Validator.validateNotEmpty(refreshToken, "refreshToken不能为空");
        // 解析refreshToken获取userId
        Integer userId = JwtUtil.parseJwt(refreshToken, Integer.class);
        // 判断token是否正确
        if (ObjectUtil.isNotNull(userId)) {
            // 获取RedisKey
            String redisKey = StrFormatter.format(RedisKeyConstants.TOKEN, userId);
            // 获取Redis中Token
            String json = stringRedisTemplate
                    .opsForValue()
                    .get(redisKey);

            // 判断json不为空
            if (StrUtil.isNotEmpty(json)) {
                // 反序列化
                Auth auth = JSONUtil.toBean(json, Auth.class);
                // 判断与redis中token是否一致
                if (auth
                        .getRefreshToken()
                        .equals(refreshToken)) {
                    // 获取最新用户信息
                    ServerResponse<User> serverResponse = userService.getUserByUserId(userId);
                    User user = serverResponse.getData();

                    // 判断账号是否禁用
                    if (user
                            .getStatus()
                            .equals(GlobalDefaultStatusEnum.DISABLE.getCode())) {
                        throw new SystemException(UpmsExceptionEnum.USER_NO_LOGIN_EXCEPTION);
                    }
                    // 判断账号是否删除
                    if (user
                            .getIsDelete()
                            .equals(GlobalDefaultDeleteEnum.DELETED.getCode())) {
                        throw new SystemException(UpmsExceptionEnum.USER_IS_DELETE_EXCEPTION);
                    }
                    return createAuth(userId);
                }
            }
        }
        throw new SystemException(AuthExceptionEnum.REFRESH_TOKEN_EXCEPTION);
    }

    /**
     * 清除token
     *
     * @param accessToken 账号token
     * @return String
     */
    @ApiOperation("清除token")
    @ApiImplicitParam(name = "accessToken", value = "账号token", required = true)
    @DeleteMapping("/removeToken")
    public String removeToken(String accessToken) {
        // 字段验证
        Validator.validateNotEmpty(accessToken, "accessToken不能为空");
        // 解析accountToken获取userId
        Integer userId = JwtUtil.parseJwt(accessToken, Integer.class);
        // 生成RedisKey
        String redisKey = StrFormatter.format(RedisKeyConstants.TOKEN, userId);
        // 清除Redis中Token
        stringRedisTemplate.delete(redisKey);
        return "清除token成功";
    }

    private Auth createAuth(Integer userId) {
        // 创建 accountToken
        String accountToken = JwtUtil.createJwt(userId, AuthConstants.ACCOUNT_TOKEN_EXPIRATION_TIME);
        // 创建 refreshToken
        String refreshToken = JwtUtil.createJwt(userId, AuthConstants.REFRESH_TOKEN_EXPIRATION_TIME);
        // 创建 redisKey
        String redisKey = StrFormatter.format(RedisKeyConstants.TOKEN, userId);
        // 创建 auth
        Auth auth = new Auth()
                .setAccessToken(accountToken)
                .setRefreshToken(refreshToken)
                .setExpiresIn(AuthConstants.ACCOUNT_TOKEN_EXPIRATION_TIME);

        stringRedisTemplate
                .opsForValue()
                .set(redisKey, JSONUtil.toJsonStr(auth), AuthConstants.REFRESH_TOKEN_EXPIRATION_TIME, TimeUnit.MILLISECONDS);
        return auth;
    }
}
