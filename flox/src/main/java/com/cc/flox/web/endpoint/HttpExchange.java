package com.cc.flox.web.endpoint;

import lombok.Data;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;

/** HTTP 交换数据类
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
