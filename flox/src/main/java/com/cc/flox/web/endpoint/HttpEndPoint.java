package com.cc.flox.web.endpoint;

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
public class HttpEndPoint {
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
    public Mono<Void> handler(HttpExchange exchange) {
        return Mono.fromRunnable(() -> {
            Object temp = flox.getRequestExtractor().apply(exchange.getRequest());
            flox.getResponseLoader().accept(temp, exchange.getResponse());
        });
    }
}
