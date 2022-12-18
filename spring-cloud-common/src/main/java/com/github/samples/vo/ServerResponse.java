package com.github.samples.vo;

import com.github.samples.enums.ResponseEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author ChengWei.Fu
 * @date 2022/12/16
 */
@Getter
@NoArgsConstructor
public class ServerResponse<T> implements Serializable {
    private Integer status;
    private String message;
    private T data;

    private ServerResponse(Integer status, String message) {
        this.status = status;
        this.message = message;
    }

    private ServerResponse(Integer status, T data) {
        this.status = status;
        this.data = data;
    }

    private ServerResponse(Integer status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> ServerResponse<T> createSuccess() {
        return new ServerResponse<>(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getMessage());
    }

    public static <T> ServerResponse<T> createSuccess(String message) {
        return new ServerResponse<>(ResponseEnum.SUCCESS.getCode(), message);
    }

    public static <T> ServerResponse<T> createSuccess(T data) {
        return new ServerResponse<>(ResponseEnum.SUCCESS.getCode(), data);
    }

    public static <T> ServerResponse<T> createSuccess(String message, T data) {
        return new ServerResponse<>(ResponseEnum.SUCCESS.getCode(), message, data);
    }

    public static <T> ServerResponse<T> createError() {
        return new ServerResponse<>(ResponseEnum.ERROR.getCode(), ResponseEnum.ERROR.getMessage());
    }

    public static <T> ServerResponse<T> createError(String message) {
        return new ServerResponse<>(ResponseEnum.ERROR.getCode(), message);
    }

    public static <T> ServerResponse<T> createError(Integer status, String message) {
        return new ServerResponse<>(status, message);
    }
}
