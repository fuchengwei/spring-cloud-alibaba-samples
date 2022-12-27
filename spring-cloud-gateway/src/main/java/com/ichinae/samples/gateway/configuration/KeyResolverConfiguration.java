package com.ichinae.samples.gateway.configuration;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * @author fuchengwei
 * @date 2020/12/22 7:53 下午
 * @description 限流配置类
 */
@Configuration
public class KeyResolverConfiguration {
    /**
     * Ip限流
     */
    @Bean
    @Primary
    public KeyResolver hostAddrKeyResolver() {
        return exchange -> Mono.just(Objects
                .requireNonNull(exchange
                        .getRequest()
                        .getRemoteAddress())
                .getHostName());
    }

    /**
     * 用户限流
     */
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> Mono.just(Objects.requireNonNull(Objects
                .requireNonNull(exchange
                        .getRequest()
                        .getHeaders()
                        .get("Authorization"))
                .get(0)));
    }

    /**
     * 接口限流
     */
    @Bean
    KeyResolver apiKeyResolver() {
        return exchange -> Mono.just(exchange
                .getRequest()
                .getPath()
                .value());
    }
}
