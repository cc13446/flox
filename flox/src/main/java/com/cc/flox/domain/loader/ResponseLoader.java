package com.cc.flox.domain.loader;


import org.springframework.http.server.reactive.ServerHttpResponse;

/**
 * @author cc
 * @date 2024/4/4
 */
public interface ResponseLoader<Source> extends Loader<Source, ServerHttpResponse> {
}
