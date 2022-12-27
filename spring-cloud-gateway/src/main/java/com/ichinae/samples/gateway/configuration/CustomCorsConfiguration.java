package com.ichinae.samples.gateway.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

/**
 * @author fuchengwei
 * @date 2020/12/22 6:10 下午
 * @description 跨域配置类
 */
@Configuration
public class CustomCorsConfiguration {
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 允许cookies跨域
        configuration.setAllowCredentials(true);
        // 允许提交请求的方法
        configuration.addAllowedMethod("*");
        // 允许向该服务器提交请求的URI
        configuration.addAllowedOrigin("*");
        // 允许访问的头信息
        configuration.addAllowedHeader("*");
        // 预检请求的缓存时间（秒），即在这个时间段里，对于相同的跨域请求不会再预检了
        configuration.setMaxAge(7200L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        source.registerCorsConfiguration("/**", configuration);

        return new CorsWebFilter(source);
    }
}
