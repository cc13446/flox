package com.cc.flox.web.router;

import com.cc.flox.web.exchange.HttpExchange;
import reactor.core.publisher.Mono;

/**
 * Http 请求路由
 *
 * @author cc
 * @date 2024/3/30
 */
public interface HttpRouter {

    /**
     * 路由 HTTP 请求
     * @param exchange exchange
     * @return 路由请求是否完成
     */
    Mono<Void> handle(HttpExchange exchange);
}