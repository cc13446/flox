package com.cc.flox.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通用 response
 *
 * @author cc
 * @date 2024/4/20
 */
@AllArgsConstructor
@Getter
public class ApiResponseWrapper<T> {

    private int code;

    private String message;

    private T data;

    /**
     * 成功
     *
     * @param data 数据
     * @param <T>  T
     * @return response
     */
    public static <T> ApiResponseWrapper<T> success(T data) {
        return new ApiResponseWrapper<>(200, "SUCCESS", data);
    }

    /**
     * @param code    code
     * @param message 信息
     * @param <T>     T
     * @return response
     */
    public static <T> ApiResponseWrapper<T> error(int code, String message) {
        return new ApiResponseWrapper<>(code, message, null);
    }

}
