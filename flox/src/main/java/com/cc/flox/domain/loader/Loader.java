package com.cc.flox.domain.loader;

import reactor.core.publisher.Mono;

/**
 * @author cc
 * @date 2024/4/4
 */
@FunctionalInterface
public interface Loader<Source, Destination> {
    Mono<Void> loader(Source source, Destination destination);
}
