package com.ichinae.samples.common.utils;

import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTVerifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fuchengwei
 * @date 2020/12/18 4:20 下午
 * @description Jwt工具类
 */
@Slf4j
public class JwtUtil {
    /**
     * 秘钥
     */
    private static final String SECRET = "Spring-Cloud-Samples-Secret";

    /**
     * 有效载荷
     */
    private static final String PAYLOAD = "payload";

    /**
     * 过期时间
     */
    private static final String EXP = "exp";

    /**
     * 创建Token
     *
     * @param object 加密实体
     * @return String
     */
    public static <T> String createJwt(T object, long expirationTime) {
        String token = "";
        try {
            final JWTSigner jwtSigner = new JWTSigner(SECRET);
            final Map<String, Object> claims = new HashMap<>(2);
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(object);
            claims.put(PAYLOAD, json);
            claims.put(EXP, System.currentTimeMillis() + expirationTime);
            token = jwtSigner.sign(claims);
        } catch (Exception e) {
            log.error("创建Token出错: {}", e.getMessage());
        }
        return token;
    }

    /**
     * 解析Token
     *
     * @param token  token
     * @param tClass 解析实体类
     * @return T
     */
    public static <T> T parseJwt(String token, Class<T> tClass) {
        final JWTVerifier jwtVerifier = new JWTVerifier(SECRET);
        try {
            final Map<String, Object> claims = jwtVerifier.verify(token);
            if (claims.containsKey(EXP) && claims.containsKey(PAYLOAD)) {
                long expirationTime = (Long) claims.get(EXP);
                long currentTime = System.currentTimeMillis();
                if (expirationTime > currentTime) {
                    String json = (String) claims.get(PAYLOAD);
                    ObjectMapper objectMapper = new ObjectMapper();
                    return objectMapper.readValue(json, tClass);
                }
            }
        } catch (Exception e) {
            log.error("解析Token出错: {}", e.getMessage());
        }
        return null;
    }
}
