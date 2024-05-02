package com.cc.flox.node;

import com.cc.flox.api.ApiManager;
import com.cc.flox.dataSource.DataSourceManager;
import com.cc.flox.domain.node.NodeType;
import com.cc.flox.domain.subFlox.impl.DefaultSubFlox;
import com.cc.flox.meta.entity.EndPointEntity;
import com.cc.flox.meta.entity.FloxEntity;
import com.cc.flox.meta.entity.NodeEntity;
import com.cc.flox.utils.AssertUtils;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 节点管理者
 *
 * @author cc
 * @date 2024/4/26
 */
@Component
public class NodeManager {

    public static final String DATA_NODE_PACKAGE_NAME = "com.cc.flox.data.node";

    /**
     * meta request extractor map
     */
    private final Map<String, NodeEntity> metaRequestExtractorMap = new ConcurrentHashMap<>();

    /**
     * meta response loader map
     */
    private final Map<String, NodeEntity> metaResponseLoaderMap = new ConcurrentHashMap<>();

    /**
     * meta node map
     */
    private final Map<String, NodeEntity> metaNodeMap = HashMap.newHashMap(10);

    /**
     * meta sub flox map
     */
    private final Map<String, NodeEntity> metaSubFloxMap = HashMap.newHashMap(10);

    /**
     * request extractor map
     */
    private volatile Map<String, NodeEntity> requestExtractorMap = Collections.emptyMap();

    /**
     * request extractor related flox
     */
    private volatile Map<String, List<String>> requestExtractorRelatedFlox = Collections.emptyMap();

    /**
     * response loader map
     */
    private volatile Map<String, NodeEntity> responseLoaderMap = Collections.emptyMap();

    /**
     * response loader related flox
     */
    private volatile Map<String, List<String>> responseLoaderRelatedFlox = Collections.emptyMap();

    /**
     * node map
     */
    private volatile Map<String, NodeEntity> nodeMap = Collections.emptyMap();

    /**
     * node related sub flox
     */
    private volatile Map<String, List<String>> nodeRelatedSubFlox = Collections.emptyMap();

    /**
     * sub flox map
     */
    private volatile Map<String, NodeEntity> subFloxMap = Collections.emptyMap();

    /**
     * sub flox related flox
     */
    private volatile Map<String, List<String>> subFloxRelatedFlox = Collections.emptyMap();

    /**
     * flox map
     */
    private volatile Map<String, FloxEntity> floxMap = Collections.emptyMap();

    /**
     * flox related endpoint
     */
    private volatile Map<String, List<String>> floxRelatedEndPoint = Collections.emptyMap();

    /**
     * endpoint map
     */
    private volatile Map<String, EndPointEntity> endPointMap = Collections.emptyMap();

    @Resource
    private ApiManager apiManager;

    @Resource
    @Lazy
    private DataSourceManager dataSourceManager;

    /**
     * @param nodeEntity node
     */
    public void putMetaNode(NodeEntity nodeEntity) {
        this.metaNodeMap.put(nodeEntity.nodeCode(), nodeEntity);
    }

    /**
     * @param subFloxCode sub flox code
     * @return 组成此子流程的节点
     */
    public List<NodeEntity> getMetaNodeBySubFlox(String subFloxCode) {
        List<NodeEntity> res = new ArrayList<>(metaNodeMap.values().stream().filter(nodeEntity -> nodeEntity.subFloxPreNodeCodeMap().containsKey(subFloxCode)).toList());
        res.addAll(metaSubFloxMap.values().stream().filter(n -> n.subFloxPreNodeCodeMap().containsKey(subFloxCode)).toList());
        return res;
    }

    /**
     * @param code           code
     * @param paramClass     参数类型
     * @param resultClass    结果类型
     * @param nodePreNodeMap 子流程依赖的节点code，及节点依赖的前置节点
     */
    public void putMetaSubFlox(String code, List<Class<?>> paramClass, Class<?> resultClass, Map<String, List<String>> nodePreNodeMap) {
        for (Map.Entry<String, List<String>> entry : nodePreNodeMap.entrySet()) {
            NodeEntity node = AssertUtils.assertNonNull(this.metaNodeMap.get(entry.getKey()), "Must depend on a non null node");
            node.subFloxPreNodeCodeMap().put(code, entry.getValue());
        }
        this.metaSubFloxMap.put(code, new NodeEntity(
                code,
                NodeType.SUB_FLOX,
                new DefaultSubFlox(
                        code,
                        this.getMetaNodeBySubFlox(code),
                        dataSourceManager),
                HashMap.newHashMap(1),
                paramClass,
                resultClass,
                HashMap.newHashMap(1)
        ));
    }

    /**
     * @param code code
     * @return sub flox
     */
    public NodeEntity getMetaSubFlox(String code) {
        return metaSubFloxMap.get(code);
    }

    /**
     * @param nodeEntity requestExtractor
     */
    public void putRequestExtract(NodeEntity nodeEntity) {
        AssertUtils.assertTrue(nodeEntity.nodeType() == NodeType.REQUEST_EXTRACTOR, "Must put type " + NodeType.REQUEST_EXTRACTOR.getCode());
        this.metaRequestExtractorMap.put(nodeEntity.nodeCode(), nodeEntity);
    }

    /**
     * @param code code
     * @return requestExtractor
     */
    public NodeEntity getRequestExtract(String code) {
        return this.metaRequestExtractorMap.get(code);
    }

    /**
     * @param nodeEntity requestExtractor
     */
    public void putResponseLoader(NodeEntity nodeEntity) {
        AssertUtils.assertTrue(nodeEntity.nodeType() == NodeType.RESPONSE_LOADER, "Must put type " + NodeType.RESPONSE_LOADER.getCode());
        this.metaResponseLoaderMap.put(nodeEntity.nodeCode(), nodeEntity);
    }

    /**
     * @param code code
     * @return ResponseLoader
     */
    public NodeEntity getResponseLoader(String code) {
        return this.metaResponseLoaderMap.get(code);
    }
}
