package com.ichinae.samples.gateway.configuration;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;

import java.util.stream.Collectors;

/**
 * @author fuchengwei
 * @date 2020/12/21 7:20 下午
 */
@Configuration
public class GatewayConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public HttpMessageConverters messageConverter(ObjectProvider<HttpMessageConverter<?>> converters) {
        return new HttpMessageConverters(converters
                .orderedStream()
                .collect(Collectors.toList()));
    }
}
