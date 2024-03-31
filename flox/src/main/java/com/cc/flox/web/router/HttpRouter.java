package com.cc.flox.web.router;

import com.cc.flox.web.endpoint.HttpEndPoint;
import com.cc.flox.web.endpoint.HttpExchange;
import reactor.core.publisher.Mono;

import java.util.concurrent.Future;

/**
 * Http 请求路由
 *
 * @author cc
 * @date 2024/3/30
 */
public interface HttpRouter {

    /**
     * 路由 HTTP 请求
     *
     * @param exchange exchange
     * @return 路由请求是否完成
     */
    Mono<Void> handle(HttpExchange exchange);

    /**
     * 添加路由
     *
     * @param endPoint endpoint
     * @return future
     */
    Future<Void> insertHandler(HttpEndPoint endPoint);

    /**
     * 删除路由
     *
     * @param key key
     * @return future*
     */
    Future<Void> deleteHandler(String key);

    /**
     * 更新路由
     *
     * @param endPoint endpoint
     * @return future*
     */
    Future<Void> updateHandler(HttpEndPoint endPoint);

}