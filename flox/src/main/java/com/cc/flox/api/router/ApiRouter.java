package com.cc.flox.api.router;

import com.cc.flox.api.endpoint.ApiEndPoint;
import com.cc.flox.api.endpoint.ApiExchange;
import reactor.core.publisher.Mono;

import java.util.concurrent.Future;

/**
 * Http 请求路由
 *
 * @author cc
 * @date 2024/3/30
 */
public interface ApiRouter {

    /**
     * 路由 HTTP 请求
     *
     * @param exchange exchange
     * @return 路由请求是否完成
     */
    Mono<Void> handle(ApiExchange exchange);

    /**
     * 添加路由
     *
     * @param endPoint endpoint
     * @return future
     */
    Future<Void> insertHandler(ApiEndPoint endPoint);

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
    Future<Void> updateHandler(ApiEndPoint endPoint);

}