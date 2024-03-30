package com.cc.flox.web;

import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

/**
 * @author cc
 * @date 2024/3/30
 */
@Component()
public class DispatchHttpHandler implements HttpHandler {

    @Override
    public @NonNull Mono<Void> handle(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response) {
        System.out.println("aa");
        return Mono.empty();
    }
}
