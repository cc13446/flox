package com.cc.flox.domain.flox;

import com.cc.flox.api.endpoint.ApiExchange;
import com.cc.flox.domain.node.NodeExecContext;
import com.cc.flox.meta.entity.NodeEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import reactor.core.publisher.Mono;

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
    private NodeEntity requestExtractor;

    /**
     * sub flox
     */
    private NodeEntity subFlox;

    /**
     * HTTP响应加载器
     */
    private NodeEntity responseLoader;

    /**
     * handler
     */
    public Mono<Void> handler(ApiExchange exchange) {
        Mono<NodeExecContext> context = Mono.just(exchange.getContext()).cache();
        Mono<Object> res = requestExtractor.exec(context, Mono.just(exchange.getRequest())).cache();
        res = subFlox.exec(context, res);
        return responseLoader.exec(context, res, Mono.just(exchange.getResponse())).mapNotNull(o -> null);
    }

}
