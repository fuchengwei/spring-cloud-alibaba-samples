package com.ichinae.samples.common.interceptor;

import cn.hutool.core.util.ObjectUtil;
import com.ichinae.samples.common.constants.Constants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author fuchengwei
 * @date 2020/12/18 7:08 下午
 * @description Feign请求拦截器
 */
public class FeignBasicAuthRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (ObjectUtil.isNotNull(servletRequestAttributes)) {
            HttpServletRequest request = servletRequestAttributes.getRequest();
            requestTemplate.header(Constants.USER_INFO, request.getHeader(Constants.USER_INFO));
        }
    }
}
