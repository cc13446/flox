package com.cc.flox.domain.transformer;

import com.cc.flox.domain.node.Node;
import com.cc.flox.domain.node.NodeExecContext;
import com.cc.flox.domain.node.NodeType;
import reactor.core.publisher.Mono;


/**
 * @author cc
 * @date 2024/4/4
 */
@FunctionalInterface
public interface BiTransformer<Source1, Source2, Result> extends Node {

    Mono<Result> transform(Mono<Source1> source1, Mono<Source2> source2, Mono<NodeExecContext> context);

    @Override
    default NodeType getType() {
        return NodeType.BI_TRANSFORMER;
    }
}
