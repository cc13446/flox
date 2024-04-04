package com.cc.flox.domain.extractor;

import reactor.core.publisher.Mono;

/**
 * @author cc
 * @date 2024/4/4
 */
@FunctionalInterface
public interface Extractor<Source, Result> {
    Mono<Result> extract(Source source);
}
