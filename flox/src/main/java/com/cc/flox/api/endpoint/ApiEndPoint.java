package com.cc.flox.api.endpoint;

import com.cc.flox.domain.Flox;
import lombok.AllArgsConstructor;
import lombok.Getter;
import reactor.core.publisher.Mono;

/**
 * HTTP 端点
 *
 * @author cc
 * @date 2024/3/31
 */
@Getter
@AllArgsConstructor
public class ApiEndPoint {
    /**
     * url path
     */
    private String path;

    /**
     * url处理流程
     */
    private Flox flox;

    /**
     * handler
     */
    public Mono<Void> handler(ApiExchange exchange) {
        return flox.getRequestExtractor().extract(exchange.getRequest())
                .flatMap(o -> flox.getResponseLoader().loader(o, exchange.getResponse()));
    }
}
