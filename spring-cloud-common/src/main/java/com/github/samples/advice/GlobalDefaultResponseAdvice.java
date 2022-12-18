package com.github.samples.advice;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.samples.enums.SystemExceptionEnum;
import com.github.samples.vo.ServerResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @author ChengWei.Fu
 * @date 2022/12/15
 * @description 全局统一响应处理
 */
@Slf4j
@ControllerAdvice(basePackages = "com.github.samples.controller")
public class GlobalDefaultResponseAdvice implements ResponseBodyAdvice<Object> {
    private final ThreadLocal<ObjectMapper> mapperThreadLocal = ThreadLocal.withInitial(ObjectMapper::new);

    @Override
    public boolean supports(@NotNull MethodParameter returnType, @NotNull Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, @NotNull MethodParameter returnType, @NotNull MediaType selectedContentType, @NotNull Class<? extends HttpMessageConverter<?>> selectedConverterType, @NotNull ServerHttpRequest request, @NotNull ServerHttpResponse response) {
        Object newBody;
        ObjectMapper objectMapper = mapperThreadLocal.get();
        response
                .getHeaders()
                .setContentType(MediaType.APPLICATION_JSON);
        if (ObjectUtil.isNull(body)) {
            newBody = ServerResponse.createSuccess();
        } else if (body instanceof ServerResponse) {
            newBody = body;
        } else if (body instanceof String) {
            try {
                newBody = objectMapper.writeValueAsString(ServerResponse.createSuccess(body.toString()));
            } catch (JsonProcessingException e) {
                log.error("全局统一响应处理异常: {}", e.getMessage());
                newBody = ServerResponse.createError(SystemExceptionEnum.TYPE_TRANSFORM_EXCEPTION.getCode(), SystemExceptionEnum.TYPE_TRANSFORM_EXCEPTION.getMessage());
            }
        } else {
            newBody = ServerResponse.createSuccess(body);
        }
        return newBody;
    }
}
