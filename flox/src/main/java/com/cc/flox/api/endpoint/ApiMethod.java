package com.cc.flox.api.endpoint;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpMethod;

import java.util.Arrays;

/**
 * http 方法
 *
 * @author cc
 * @date 2024/4/12
 */
@AllArgsConstructor
@Getter
public enum ApiMethod {
    GET("GET", HttpMethod.GET),
    POST("POST", HttpMethod.POST),
    PUT("PUT", HttpMethod.PUT),
    DELETE("DELETE", HttpMethod.DELETE);

    /**
     * code
     */
    private final String code;

    /**
     * http method
     */
    private final HttpMethod httpMethod;

    public static ApiMethod fromHttpMethod(HttpMethod method) {
        return Arrays.stream(ApiMethod.values()).filter(apiMethod -> apiMethod.getHttpMethod().equals(method)).findFirst().orElse(null);
    }

    public static ApiMethod fromCode(String code) {
        return Arrays.stream(ApiMethod.values()).filter(apiMethod -> apiMethod.getCode().equals(code)).findFirst().orElse(null);
    }
}
