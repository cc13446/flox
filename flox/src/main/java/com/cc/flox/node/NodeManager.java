package com.cc.flox.node;

import com.cc.flox.api.ApiManager;
import com.cc.flox.meta.entity.EndPointEntity;
import com.cc.flox.meta.entity.FloxEntity;
import com.cc.flox.meta.entity.NodeEntity;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 节点管理者
 *
 * @author cc
 * @date 2024/4/26
 */
@Component
public class NodeManager {

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

}
