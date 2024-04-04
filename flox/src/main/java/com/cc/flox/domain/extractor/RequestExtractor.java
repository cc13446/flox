package com.cc.flox.domain.extractor;

import org.springframework.http.server.reactive.ServerHttpRequest;


/**
 * HTTP 请求 Extractor
 *
 * @author cc
 * @date 2024/4/4
 */
@FunctionalInterface
public interface RequestExtractor<Result> extends Extractor<ServerHttpRequest, Result> {

}
