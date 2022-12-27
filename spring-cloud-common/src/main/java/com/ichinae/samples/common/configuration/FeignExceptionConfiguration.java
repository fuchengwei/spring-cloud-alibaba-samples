package com.ichinae.samples.common.configuration;

import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ichinae.samples.common.enums.ResponseEnum;
import com.ichinae.samples.common.exception.SystemException;
import com.ichinae.samples.common.vo.ServerResponse;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author fuchengwei
 * @date 2020/12/18 6:47 下午
 * @description Feign异常处理配置类
 */
@Slf4j
@Configuration
public class FeignExceptionConfiguration {
    @Bean
    public ErrorDecoder errorDecoder() {
        return new AuthErrorDecoder();
    }

    public static class AuthErrorDecoder implements ErrorDecoder {
        @Override
        public Exception decode(String s, Response response) {
            Exception exception = null;
            ObjectMapper objectMapper = new ObjectMapper();
            // 空属性处理
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
            // 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            // 禁止使用int代表enum的order来反序列化enum
            objectMapper.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, true);
            try {
                String json = Util.toString(response
                        .body()
                        .asReader(StandardCharsets.UTF_8));
                exception = new RuntimeException(json);
                if (StrUtil.isEmpty(json)) {
                    return null;
                }
                ServerResponse<?> serverResponse = objectMapper.readValue(json, ServerResponse.class);
                if (response.status() == HttpServletResponse.SC_INTERNAL_SERVER_ERROR) {
                    exception = new Exception(serverResponse.getMessage());
                } else if (response.status() == HttpServletResponse.SC_OK && !serverResponse
                        .getStatus()
                        .equals(ResponseEnum.SUCCESS.getCode())) {
                    exception = new SystemException(serverResponse.getStatus(), serverResponse.getMessage());
                } else if (response.status() == HttpServletResponse.SC_BAD_REQUEST) {
                    exception = new ValidateException(serverResponse.getMessage());
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
            return exception;
        }
    }
}
