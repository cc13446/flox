package com.cc.flox.api;

import com.cc.flox.api.endpoint.ApiExchange;
import com.cc.flox.api.router.ApiRouter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

/**
 * Http 请求处理器
 *
 * @author cc
 * @date 2024/3/30
 */
@Component
@Slf4j
public class DispatchHandler implements HttpHandler {

    @Resource
    private ApiRouter trieApiRouter;

    @Override
    public @NonNull Mono<Void> handle(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response) {
        log.info("Recv http request path [{}]", request.getPath().value());
        ApiExchange exchange = new ApiExchange();
        exchange.setRequest(request);
        exchange.setResponse(response);
        return trieApiRouter.handle(exchange);
    }
}
