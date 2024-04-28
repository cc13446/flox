package com.cc.flox.domain.transformer;

import com.cc.flox.domain.node.Node;
import com.cc.flox.domain.node.NodeType;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author cc
 * @date 2024/4/4
 */
@FunctionalInterface
public interface Transformer<Source, Result> extends Node {

    Mono<Result> transform(Mono<Source> source, Mono<Map<String, Object>> attribute);

    @Override
    default NodeType getType() {
        return NodeType.TRANSFORMER;
    }
}
