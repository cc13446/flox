package com.cc.flox.domain.transformer;

import reactor.core.publisher.Mono;

/**
 * @author cc
 * @date 2024/4/4
 */
@FunctionalInterface
public interface Transformer<Source, Result> {

    Mono<Result> transform(Source source);
}
