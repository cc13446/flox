package com.cc.flox.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 流程
 *
 * @author cc
 * @date 2024/3/31
 */
@AllArgsConstructor
@Getter
public class Flox {

    /**
     * HTTP请求提取器
     */
    private Function<ServerHttpRequest, Object> requestExtractor;

    /**
     * HTTP响应加载器
     */
    private BiConsumer<Object, ServerHttpResponse> responseLoader;

}
