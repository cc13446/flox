package com.cc.flox.domain.subFlox.impl;

import com.cc.flox.domain.subFlox.SubFlox;
import com.cc.flox.meta.entity.NodeEntity;
import com.cc.flox.utils.AssertUtils;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 默认子流程
 *
 * @author cc
 * @date 2024/4/17
 */
public class DefaultSubFlox implements SubFlox {

    /**
     * 参数类型
     */
    private final Class<?> sourceClass;

    /**
     * 结果类型
     */
    private final Class<?> resultClass;

    /**
     * 子流程中所有的节点
     */
    private final Map<String, NodeEntity> nodeMap;

    public DefaultSubFlox(Class<?> sourceClass, Class<?> resultClass, List<NodeEntity> nodeEntities) {
        this.sourceClass = sourceClass;
        this.resultClass = resultClass;
        this.nodeMap = nodeEntities.stream().collect(Collectors.toMap(NodeEntity::nodeCode, n -> n));
    }



    @Override
    public Mono<Object> handle(Object object) {
        AssertUtils.assertTrue(sourceClass.isAssignableFrom(object.getClass()), "SubFlox must handle [" + sourceClass.getCanonicalName() + "] but handle [" + object.getClass().getCanonicalName() + "]");

        return null;
    }
}
