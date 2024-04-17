package com.cc.flox.domain.transformer;

import com.cc.flox.domain.node.Node;
import com.cc.flox.domain.node.NodeType;
import reactor.core.publisher.Mono;

/**
 * @author cc
 * @date 2024/4/4
 */
@FunctionalInterface
public interface TriTransformer<Source1, Source2, Source3, Result> extends Node {

    Mono<Result> transform(Source1 source1, Source2 source2, Source3 source3);

    @Override
    default NodeType getType() {
        return NodeType.TRI_TRANSFORMER;
    }
}
