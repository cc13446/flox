package com.cc.flox.node;

import com.cc.flox.api.ApiManager;
import com.cc.flox.domain.node.NodeType;
import com.cc.flox.meta.entity.EndPointEntity;
import com.cc.flox.meta.entity.FloxEntity;
import com.cc.flox.meta.entity.NodeEntity;
import com.cc.flox.utils.AssertUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 节点管理者
 *
 * @author cc
 * @date 2024/4/26
 */
@Component
public class NodeManager {

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
     * @param nodeEntity sub flox
     */
    public void putMetaSubFlox(NodeEntity nodeEntity) {
        AssertUtils.assertTrue(nodeEntity.nodeType() == NodeType.SUB_FLOX, "Must put sub flox type");
        this.metaSubFloxMap.put(nodeEntity.nodeCode(), nodeEntity);
    }

    /**
     * @param code code
     * @return sub flox
     */
    public NodeEntity getMetaSubFlox(String code) {
        return metaSubFloxMap.get(code);
    }

}
