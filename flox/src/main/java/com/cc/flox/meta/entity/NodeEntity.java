package com.cc.flox.meta.entity;

import com.cc.flox.domain.node.Node;
import com.cc.flox.domain.node.NodeType;

import java.util.List;
import java.util.Map;

/**
 * 节点
 *
 * @author cc
 * @date 2024/4/17
 */
public record NodeEntity(

        String nodeCode,

        NodeType nodeType,

        Node node,

        Map<String, Object> attribute,

        List<Class<?>> paramClassList,

        Class<?> resultClass,

        Map<String, List<String>> subFloxPreNodeCodeMap) {
}
