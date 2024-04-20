package com.cc.flox.domain.flox;

import com.cc.flox.api.endpoint.ApiExchange;
import com.cc.flox.domain.extractor.RequestExtractor;
import com.cc.flox.domain.loader.ResponseLoader;
import com.cc.flox.domain.subFlox.SubFlox;
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
    private RequestExtractor<Object> requestExtractor;

    /**
     * sub flox
     */
    private SubFlox subFlox;

    /**
     * HTTP响应加载器
     */
    private ResponseLoader<Object> responseLoader;

    /**
     * handler
     */
    public Mono<Void> handler(ApiExchange exchange) {
        Mono<Object> res = requestExtractor.extract(Mono.just(exchange.getRequest()));
        res = subFlox.handle(res);
        return responseLoader.loader(res, Mono.just(exchange.getResponse()));
    }

}
