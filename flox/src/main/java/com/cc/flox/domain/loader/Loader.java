package com.cc.flox.domain.loader;

import com.cc.flox.domain.node.Node;
import com.cc.flox.domain.node.NodeType;
import reactor.core.publisher.Mono;

/**
 * @author cc
 * @date 2024/4/4
 */
@FunctionalInterface
public interface Loader<Source, Destination, Result> extends Node {
    Mono<Result> loader(Mono<Source> source, Mono<Destination> destination);

    @Override
    default NodeType getType() {
        return NodeType.LOADER;
    }
}
