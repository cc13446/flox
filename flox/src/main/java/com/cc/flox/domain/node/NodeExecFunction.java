package com.cc.flox.domain.node;

import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author cc
 * @date 2024/4/20
 */
@FunctionalInterface
public interface NodeExecFunction {
    Mono<Object> exec(Node node, List<Mono<Object>> param);
}
