package com.cc.flox.api;

import com.cc.flox.api.endpoint.ApiExchange;
import com.cc.flox.domain.node.NodeExecContext;
import com.cc.flox.node.FloxWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

import java.util.List;
import java.util.UUID;

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
    private ApiManager apiManager;

    @Resource
    private FloxWrapper floxWrapper;

    @Override
    public @NonNull Mono<Void> handle(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response) {
        log.info("Receive http request path [{}] method [{}]", request.getPath().value(), request.getMethod());
        // cors
        response.getHeaders().putIfAbsent(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, List.of("*"));
        // cors 预检
        if (HttpMethod.OPTIONS.equals(request.getMethod()) && request.getHeaders().containsKey(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD)) {
            response.getHeaders().putIfAbsent(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, List.of("GET", "POST", "PUT", "DELETE"));
            response.getHeaders().putIfAbsent(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, List.of("Content-Type", "Access-Control-Allow-Origin"));
            response.getHeaders().putIfAbsent(HttpHeaders.ACCESS_CONTROL_MAX_AGE, List.of("86400"));
            response.setStatusCode(HttpStatus.OK);
            return response.setComplete();
        }
        ApiExchange exchange = new ApiExchange();
        exchange.setRequest(request);
        exchange.setResponse(response);
        exchange.setContext(new NodeExecContext(UUID.randomUUID().toString()));
        return floxWrapper.wrap(exchange, () -> apiManager.handle(exchange));
    }
}
