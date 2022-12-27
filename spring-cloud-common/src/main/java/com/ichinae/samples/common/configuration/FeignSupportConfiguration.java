package com.ichinae.samples.common.configuration;

import com.ichinae.samples.common.interceptor.FeignBasicAuthRequestInterceptor;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author fuchengwei
 * @date 2020/12/18 7:12 下午
 * @description Feign配置全局注册
 */
@Configuration
public class FeignSupportConfiguration {
    @Bean
    public RequestInterceptor requestInterceptor() {
        return new FeignBasicAuthRequestInterceptor();
    }
}
