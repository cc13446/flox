package com.cc.flox.web.endpoint;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Consumer;

/**
 * HTTP 端点
 *
 * @author cc
 * @date 2024/3/31
 */
@Getter
@AllArgsConstructor
public class HttpEndPoint {
    /**
     * url path
     */
    private String path;

    /**
     * handler
     */
    private Consumer<HttpExchange> handler;
}
