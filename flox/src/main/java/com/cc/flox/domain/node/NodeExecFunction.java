package com.cc.flox.domain.node;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * @author cc
 * @date 2024/4/20
 */
@FunctionalInterface
public interface NodeExecFunction {
    Mono<Object> exec(Node node, List<Mono<Object>> param, Mono<Map<String, Object>> attribute);
}
