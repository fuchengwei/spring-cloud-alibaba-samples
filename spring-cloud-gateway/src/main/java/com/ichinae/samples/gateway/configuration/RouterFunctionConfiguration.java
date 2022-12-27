package com.ichinae.samples.gateway.configuration;

import com.ichinae.samples.gateway.handle.SwaggerResourceHandler;
import com.ichinae.samples.gateway.handle.SwaggerSecurityHandler;
import com.ichinae.samples.gateway.handle.SwaggerUiHandler;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;

/**
 * @author fuchengwei
 * @date 2020/12/22 6:30 下午
 * @description 路由配置信息
 */
@Configuration
@AllArgsConstructor
public class RouterFunctionConfiguration {
    private final SwaggerResourceHandler swaggerResourceHandler;
    private final SwaggerSecurityHandler swaggerSecurityHandler;
    private final SwaggerUiHandler swaggerUiHandler;

    @Bean
    public RouterFunction<?> routerFunction() {
        return RouterFunctions
                .route(
                        RequestPredicates
                                .path("/swagger-resources")
                                .and(RequestPredicates.accept(MediaType.ALL)), swaggerResourceHandler)
                .andRoute(RequestPredicates
                        .GET("/swagger-resources/configuration/ui")
                        .and(RequestPredicates.accept(MediaType.ALL)), swaggerUiHandler)
                .andRoute(RequestPredicates
                        .GET("/swagger-resources/configuration/security")
                        .and(RequestPredicates.accept(MediaType.ALL)), swaggerSecurityHandler);
    }
}
