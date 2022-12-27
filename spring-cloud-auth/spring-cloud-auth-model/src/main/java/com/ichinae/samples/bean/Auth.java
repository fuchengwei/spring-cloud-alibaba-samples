package com.ichinae.samples.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author fuchengwei
 * @date 2020/12/18 4:44 下午
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Auth implements Serializable {
    /**
     * 账号token
     */
    private String accessToken;

    /**
     * 刷新token
     */
    private String refreshToken;

    /**
     * token类型：默认 Bearer
     */
    private String tokenType = "Bearer";

    /**
     * 过期时间（毫秒）
     */
    private long expiresIn;
}
