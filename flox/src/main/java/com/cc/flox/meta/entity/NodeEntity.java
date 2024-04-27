package com.cc.flox.meta.entity;

import com.cc.flox.domain.node.Node;
import com.cc.flox.domain.node.NodeType;

import java.util.HashMap;
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

    public NodeEntity(String nodeCode, NodeType nodeType, Node node) {
        this(nodeCode, nodeType, node, HashMap.newHashMap(1), List.of(Map.class), Map.class, HashMap.newHashMap(1));
    }

}
