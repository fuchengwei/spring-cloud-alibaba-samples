package com.ichinae.samples.common.advice;

import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.json.JSONUtil;
import com.ichinae.samples.common.exception.SystemException;
import com.ichinae.samples.common.vo.ServerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @author fuchengwei
 * @date 2020/12/18 4:08 下午
 * @description 全局统一异常处理
 */
@Slf4j
@RestControllerAdvice
public class GlobalDefaultExceptionAdvice {
    @ExceptionHandler(value = SystemException.class)
    public void errorHandler(HttpServletResponse response, SystemException exception) throws Exception {
        log.error("系统异常拦截：{}", exception.getMessage());
        response.setContentType("application/json;charset=utf-8");
        // 状态码 200：请求成功，系统异常
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter writer = response.getWriter();
        writer.write(JSONUtil.toJsonPrettyStr(ServerResponse.createByErrorCodeMessage(exception.getCode(), exception.getMessage())));
        writer.flush();
        writer.close();
    }

    @ExceptionHandler(value = ValidateException.class)
    public void errorHandle(HttpServletResponse response, ValidateException exception) throws Exception {
        log.error("请求参数异常拦截：{}", exception.getMessage());
        response.setContentType("application/json;charset=utf-8");
        // 状态码 400：请求参数异常
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        PrintWriter writer = response.getWriter();
        writer.write(JSONUtil.toJsonPrettyStr(ServerResponse.createByErrorMessage(exception.getMessage())));
        writer.flush();
        writer.close();
    }

    @ExceptionHandler(value = Exception.class)
    public void errorHandle(HttpServletResponse response, Exception exception) throws Exception {
        log.error("全局异常拦截：{}", exception.getMessage());
        exception.printStackTrace();
        response.setContentType("application/json;charset=utf-8");
        // 状态码 500：系统异常
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        PrintWriter writer = response.getWriter();
        writer.write(JSONUtil.toJsonPrettyStr(ServerResponse.createByErrorMessage(exception.getMessage())));
        writer.flush();
        writer.close();
    }
}
