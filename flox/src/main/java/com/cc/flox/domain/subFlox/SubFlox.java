package com.cc.flox.domain.subFlox;

import com.cc.flox.domain.node.Node;
import com.cc.flox.domain.node.NodeType;
import reactor.core.publisher.Mono;

/**
 * 子流程
 *
 * @author cc
 * @date 2024/4/17
 */
@FunctionalInterface
public interface SubFlox extends Node {

    /**
     * @param object 参数
     * @return 结果
     */
    Mono<Object> handle(Object object);

    @Override
    default NodeType getType() {
        return NodeType.SUB_FLOX;
    }

}
