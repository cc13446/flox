package com.cc.flox.node;

import com.cc.flox.api.ApiManager;
import com.cc.flox.dataSource.DataSourceManager;
import com.cc.flox.domain.extractor.RequestExtractor;
import com.cc.flox.domain.loader.ResponseLoader;
import com.cc.flox.domain.node.NodeType;
import com.cc.flox.domain.subFlox.impl.DefaultSubFlox;
import com.cc.flox.meta.entity.EndPointEntity;
import com.cc.flox.meta.entity.FloxEntity;
import com.cc.flox.meta.entity.NodeEntity;
import com.cc.flox.utils.AssertUtils;
import jakarta.annotation.Resource;
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

    /**
     * meta request extractor map
     */
    private final Map<String, RequestExtractor<Object>> requestExtractorMap = new ConcurrentHashMap<>();

    /**
     * meta response loader map
     */
    private final Map<String, ResponseLoader<Object>> responseLoaderMap = new ConcurrentHashMap<>();

    /**
     * meta node map
     */
    private final Map<String, NodeEntity> metaNodeMap = HashMap.newHashMap(10);

    /**
     * meta sub flox map
     */
    private final Map<String, NodeEntity> metaSubFloxMap = HashMap.newHashMap(10);

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
                        paramClass.getFirst(),
                        resultClass,
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
     * @param code             code
     * @param requestExtractor requestExtractor
     */
    public void putRequestExtract(String code, RequestExtractor<Object> requestExtractor) {
        this.requestExtractorMap.put(code, requestExtractor);
    }

    /**
     * @param code code
     * @return requestExtractor
     */
    public RequestExtractor<Object> getRequestExtract(String code) {
        return this.requestExtractorMap.get(code);
    }

    /**
     * @param code           code
     * @param responseLoader responseLoader
     */
    public void putResponseLoader(String code, ResponseLoader<Object> responseLoader) {
        this.responseLoaderMap.put(code, responseLoader);
    }

    /**
     * @param code code
     * @return ResponseLoader
     */
    public ResponseLoader<Object> getResponseLoader(String code) {
        return this.responseLoaderMap.get(code);
    }
}
