package com.cc.flox.meta.entity;

import com.cc.flox.domain.node.Node;
import com.cc.flox.domain.node.NodeExecContext;
import com.cc.flox.domain.node.NodeType;
import com.cc.flox.utils.AssertUtils;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    public NodeEntity(String nodeCode, NodeType nodeType, Node node, List<Class<?>> paramClass, Class<?> resultClass) {
        this(nodeCode, nodeType, node, HashMap.newHashMap(1), paramClass, resultClass, HashMap.newHashMap(1));
    }

    /**
     * @param context 上下文
     * @param param   参数
     * @return node 执行结果
     */
    public Mono<Object> exec(Mono<NodeExecContext> context, Mono<Object> param) {
        return exec(context, List.of(param));
    }

    /**
     * @param context 上下文
     * @param param1  参数
     * @param param2  参数
     * @return node 执行结果
     */
    public Mono<Object> exec(Mono<NodeExecContext> context, Mono<Object> param1, Mono<Object> param2) {
        return exec(context, List.of(param1, param2));
    }

    /**
     * @param context 上下文
     * @param params  参数
     * @return node 执行结果
     */
    public Mono<Object> exec(Mono<NodeExecContext> context, List<Mono<Object>> params) {
        // 检查参数
        List<Mono<Object>> checkedParams = IntStream.range(0, nodeType.getParamSize()).mapToObj(i -> {
            Mono<Object> param = params.get(i);
            Class<?> paramClass = paramClassList.get(i);
            return param.doOnNext(o -> AssertUtils.assertTrue(paramClass.isAssignableFrom(o.getClass()), "Node [" + nodeCode + "]must handle [" + paramClass.getCanonicalName() + "] but handle [" + o.getClass().getCanonicalName() + "]"));
        }).collect(Collectors.toList());

        // 计算
        context = context.doOnNext(c -> c.setAttribute(attribute));
        Mono<Object> result = nodeType.getExecFunction().exec(node, checkedParams, context);

        // 检查结果
        return result.doOnNext(o -> AssertUtils.assertTrue(resultClass.isAssignableFrom(o.getClass()), "Node [" + nodeCode + "] must return [" + resultClass.getCanonicalName() + "] but return [" + o.getClass().getCanonicalName() + "]"));
    }

}
