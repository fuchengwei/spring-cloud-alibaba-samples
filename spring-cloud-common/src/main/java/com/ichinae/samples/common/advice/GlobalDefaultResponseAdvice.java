package com.ichinae.samples.common.advice;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ichinae.samples.common.enums.SystemExceptionEnum;
import com.ichinae.samples.common.vo.ServerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @author fuchengwei
 * @date 2020/12/18 11:08 上午
 * @description 全局统一响应处理
 */
@Slf4j
@ControllerAdvice(basePackages = "com.ichinae.samples.controller")
public class GlobalDefaultResponseAdvice implements ResponseBodyAdvice<Object> {
    private final ThreadLocal<ObjectMapper> mapperThreadLocal = ThreadLocal.withInitial(ObjectMapper::new);

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Object beforeBodyWrite(Object object, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        Object obj;
        ObjectMapper objectMapper = mapperThreadLocal.get();
        serverHttpResponse
                .getHeaders()
                .setContentType(MediaType.APPLICATION_JSON);
        if (ObjectUtil.isNull(object)) {
            obj = ServerResponse.createBySuccess();
        } else if (object instanceof String) {
            try {
                obj = objectMapper.writeValueAsString(ServerResponse.createBySuccessMessage(object.toString()));
            } catch (JsonProcessingException e) {
                log.error("全局统一响应处理出错：{}", e.getMessage());
                obj = ServerResponse.createByErrorCodeMessage(SystemExceptionEnum.TYPE_TRANSFORM_EXCEPTION.getCode(), SystemExceptionEnum.TYPE_TRANSFORM_EXCEPTION.getMessage());
            }
        } else if (object instanceof ServerResponse) {
            obj = object;
        } else {
            obj = ServerResponse.createBySuccess(object);
        }
        return obj;
    }
}
