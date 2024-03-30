package com.cc.flox.web.exchange;

import lombok.Data;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;

/**
 * @author cc
 * @date 2024/3/30
 */
@Data
public class HttpExchange {

    /**
     * HTTP 请求
     */
    private ServerHttpRequest request;

    /**
     * HTTP 响应
     */
    private ServerHttpResponse response;

}
