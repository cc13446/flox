package com.cc.flox.result;

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
public class Response<T> {

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
    public static <T> Response<T> success(T data) {
        return new Response<>(200, "SUCCESS", data);
    }

}
