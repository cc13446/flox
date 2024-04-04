package com.cc.flox.domain.transformer;

import reactor.core.publisher.Mono;

/**
 * @author cc
 * @date 2024/4/4
 */
@FunctionalInterface
public interface BiTransformer<Source1, Source2, Result> {

    Mono<Result> transform(Source1 source1, Source2 source2);
}
