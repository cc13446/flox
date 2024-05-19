package com.cc.flox.domain.subFlox.impl;

import com.cc.flox.dataSource.DataSourceManager;
import com.cc.flox.domain.node.NodeExecContext;
import com.cc.flox.domain.subFlox.SubFlox;
import com.cc.flox.meta.entity.NodeEntity;
import com.cc.flox.utils.AssertUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 默认子流程
 *
 * @author cc
 * @date 2024/4/17
 */
public class DefaultSubFlox implements SubFlox {

    /**
     * 代表参数的 node code
     */
    public static final String PRE_NODE_CODE_PARAM = "PRE_PARAM";

    /**
     * 代表 data source manager 的 node code
     */
    public static final String PRE_NODE_CODE_DATA_SOURCE_MANAGER = "PRE_DATA_SOURCE_MANAGER";

    /**
     * code
     */
    private final String code;

    /**
     * 子流程中所有的节点
     */
    private final Map<String, NodeEntity> nodeMap;

    /**
     * 数据源管理器
     */
    private final DataSourceManager dataSourceManager;

    public DefaultSubFlox(String code, List<NodeEntity> nodeEntities, DataSourceManager manager) {
        this.code = code;
        this.nodeMap = nodeEntities.stream().collect(Collectors.toMap(NodeEntity::nodeCode, n -> n));
        this.dataSourceManager = manager;
        AssertUtils.assertTrue(nodeEntities.stream().allMatch(e -> e.subFloxPreNodeCodeMap().containsKey(code)), "Node must have preNode in the subflox");
    }

    @Override
    public Mono<Object> handle(Mono<Object> param, Mono<NodeExecContext> context) {
        AssertUtils.assertTrue(!CollectionUtils.isEmpty(nodeMap), "SubFlox must have nodes");

        Map<String, List<String>> preNodeMap = nodeMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().subFloxPreNodeCodeMap().get(code)));
        Map<String, List<String>> postNodeMap = getPostNodeMap(preNodeMap);

        checkNodeMap(preNodeMap, postNodeMap);

        return execNodes(param, context, preNodeMap, postNodeMap);
    }

    /**
     * @param preNodeMap pre node map
     * @return post node map
     */
    private Map<String, List<String>> getPostNodeMap(Map<String, List<String>> preNodeMap) {
        Map<String, List<String>> result = preNodeMap.entrySet().stream()
                .flatMap(e -> e.getValue().stream().map(v -> new MutablePair<>(v, e.getKey())))
                .collect(Collectors.groupingBy(MutablePair::getLeft, Collectors.mapping(MutablePair::getRight, Collectors.toList())));
        for (Map.Entry<String, List<String>> entry : preNodeMap.entrySet()) {
            result.putIfAbsent(entry.getKey(), new ArrayList<>());
        }
        return result;
    }

    /**
     * @param param       参数
     * @param context     执行上下文
     * @param preNodeMap  pre node map
     * @param postNodeMap post node map
     * @return 执行结果
     */
    private Mono<Object> execNodes(Mono<Object> param,
                                   Mono<NodeExecContext> context,
                                   Map<String, List<String>> preNodeMap,
                                   Map<String, List<String>> postNodeMap) {
        Map<String, Mono<Object>> execResultMap = HashMap.newHashMap(nodeMap.size());
        Stack<String> execStack = new Stack<>();
        String root = postNodeMap.entrySet().stream().filter(e -> CollectionUtils.isEmpty(e.getValue())).map(Map.Entry::getKey).findFirst().orElseThrow();
        execStack.push(root);
        while (!CollectionUtils.isEmpty(execStack)) {
            if (execResultMap.containsKey(execStack.peek())) {
                execStack.pop();
                continue;
            }

            Set<String> preNodes = new HashSet<>(preNodeMap.getOrDefault(execStack.peek(), Collections.emptyList()));
            preNodes.removeAll(execResultMap.keySet());
            preNodes.remove(PRE_NODE_CODE_PARAM);
            preNodes.remove(PRE_NODE_CODE_DATA_SOURCE_MANAGER);
            if (CollectionUtils.isEmpty(preNodes)) {
                NodeEntity nodeEntity = AssertUtils.assertNonNull(nodeMap.get(execStack.peek()), "Node [" + execStack.peek() + "] cannot be null");
                List<Mono<Object>> p = new ArrayList<>(nodeEntity.nodeType().getParamSize());
                switch (nodeEntity.nodeType()) {
                    case SUB_FLOX, EXTRACTOR, LOADER, TRANSFORMER, BI_TRANSFORMER, TRI_TRANSFORMER, DATA_SOURCE_LOADER -> {
                        for (String preNode : nodeEntity.subFloxPreNodeCodeMap().get(code)) {
                            p.add(getExecResult(preNode, execResultMap, param));
                        }
                    }
                    default -> throw new RuntimeException("Invalid node type : " + nodeEntity.nodeType());
                }
                Mono<Object> result = nodeEntity.exec(context, p).cache();
                execResultMap.put(execStack.peek(), result);
                execStack.pop();
            } else {
                for (String nodeToExec : preNodes) {
                    execStack.push(nodeToExec);
                }
            }
        }
        return AssertUtils.assertNonNull(execResultMap.get(root), "SubFlox result cannot be null");
    }

    /**
     * @param nodeCode      node code
     * @param execResultMap 执行结果 map
     * @param param         子流程参数
     * @return node code 对应的结果
     */
    private Mono<Object> getExecResult(String nodeCode, Map<String, Mono<Object>> execResultMap, Mono<Object> param) {
        if (PRE_NODE_CODE_PARAM.equals(nodeCode)) {
            return param;
        }
        if (PRE_NODE_CODE_DATA_SOURCE_MANAGER.equals(nodeCode)) {
            return Mono.just(dataSourceManager);
        }
        return execResultMap.get(nodeCode);
    }

    /**
     * 检查 node map 的有效性
     *
     * @param preNodeMap  pre node map
     * @param postNodeMap post node map
     */
    private void checkNodeMap(Map<String, List<String>> preNodeMap, Map<String, List<String>> postNodeMap) {
        for (NodeEntity entity : nodeMap.values()) {
            AssertUtils.assertTrue(entity.nodeType().getParamSize() == entity.subFloxPreNodeCodeMap().get(code).size(), entity.nodeType().getCode() + " must have just [" + entity.nodeType().getParamSize() + "] param");
        }

        // 只有一个树根
        List<String> rootList = postNodeMap.entrySet().stream().filter(e -> CollectionUtils.isEmpty(e.getValue())).map(Map.Entry::getKey).toList();
        AssertUtils.assertTrue(rootList.size() == 1, "SubFlox must just have one root");

        // 检查无环
        String root = rootList.getFirst();
        List<String> path = new LinkedList<>();
        dfs(root, path, preNodeMap);

    }

    /**
     * @param root       root
     * @param path       路径
     * @param preNodeMap 映射关系
     */
    private void dfs(String root, List<String> path, Map<String, List<String>> preNodeMap) {
        AssertUtils.assertTrue(!path.contains(root), "SubFlox has circle");
        path.add(root);
        List<String> next = preNodeMap.get(root);
        if (!CollectionUtils.isEmpty(next)) {
            for (String n : next) {
                dfs(n, path, preNodeMap);
            }
        }
        path.removeLast();
    }
}
