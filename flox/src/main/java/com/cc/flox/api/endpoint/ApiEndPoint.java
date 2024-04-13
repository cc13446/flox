package com.cc.flox.api.endpoint;

import com.cc.flox.domain.flox.Flox;
import reactor.core.publisher.Mono;

/**
 * HTTP 端点
 *
 * @param path   url path
 * @param method method
 * @param flox   url处理流程
 * @author cc
 * @date 2024/3/31
 */
public record ApiEndPoint(String path, ApiMethod method, Flox flox) {

    /**
     * handler
     */
    public Mono<Void> handler(ApiExchange exchange) {
        return flox.getRequestExtractor().extract(exchange.getRequest())
                .flatMap(o -> flox.getResponseLoader().loader(o, exchange.getResponse()));
    }
}
