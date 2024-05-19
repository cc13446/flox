package com.cc.flox.domain.extractor;

import com.cc.flox.domain.node.Node;
import com.cc.flox.domain.node.NodeExecContext;
import com.cc.flox.domain.node.NodeType;
import reactor.core.publisher.Mono;


/**
 * @author cc
 * @date 2024/4/4
 */
@FunctionalInterface
public interface Extractor<Source, Result> extends Node {

    Mono<Result> extract(Mono<Source> source, Mono<NodeExecContext> context);

    @Override
    default NodeType getType() {
        return NodeType.EXTRACTOR;
    }
}
