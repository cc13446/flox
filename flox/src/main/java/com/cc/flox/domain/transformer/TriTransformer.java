package com.cc.flox.domain.transformer;

import reactor.core.publisher.Mono;

/**
 * @author cc
 * @date 2024/4/4
 */
@FunctionalInterface
public interface TriTransformer<Source1, Source2, Source3, Result> {

    Mono<Result> transform(Source1 source1, Source2 source2, Source3 source3);
}
