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
    GET(HttpMethod.GET),
    POST(HttpMethod.POST),
    PUT(HttpMethod.PUT),
    DELETE(HttpMethod.DELETE);

    /**
     * http method
     */
    private final HttpMethod httpMethod;

    public static ApiMethod fromHttpMethod(HttpMethod method) {
        return Arrays.stream(ApiMethod.values()).filter(apiMethod -> apiMethod.getHttpMethod().equals(method)).findFirst().orElse(null);
    }
}
