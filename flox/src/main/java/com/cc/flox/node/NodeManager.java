package com.cc.flox.node;

import com.cc.flox.api.ApiManager;
import com.cc.flox.api.endpoint.ApiEndPoint;
import com.cc.flox.api.endpoint.ApiMethod;
import com.cc.flox.dataSource.DataSourceManager;
import com.cc.flox.dataType.DataTypeClassLoader;
import com.cc.flox.domain.flox.FloxBuilder;
import com.cc.flox.domain.node.Node;
import com.cc.flox.domain.node.NodeExecContext;
import com.cc.flox.domain.node.NodeType;
import com.cc.flox.domain.subFlox.impl.DefaultSubFlox;
import com.cc.flox.meta.Constant;
import com.cc.flox.meta.entity.EndPointEntity;
import com.cc.flox.meta.entity.FloxEntity;
import com.cc.flox.meta.entity.NodeEntity;
import com.cc.flox.utils.AssertUtils;
import com.cc.flox.utils.GroovyCodeUtils;
import com.cc.flox.utils.GsonUtils;
import com.google.gson.reflect.TypeToken;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.cc.flox.initializer.meta.MetaSubFloxInitializer.META_SUB_FLOX_CODE_CONCAT_NODE_FLOX_ENDPOINT;
import static com.cc.flox.utils.FormatUtils.YYYY_MM_DD_HH_MM_SS;

/**
 * 节点管理者
 *
 * @author cc
 * @date 2024/4/26
 */
@Component
@Slf4j
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
    private volatile Map<String, Map<String, List<String>>> nodeRelatedSubFlox = Collections.emptyMap();

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

    /**
     * 是否启动
     */
    private final AtomicBoolean hasStart = new AtomicBoolean(false);

    /**
     * 更新时间
     */
    private final AtomicReference<OffsetDateTime> updateTime = new AtomicReference<>(OffsetDateTime.MIN);

    @Resource
    private ApiManager apiManager;

    @Resource
    @Lazy
    private DataSourceManager dataSourceManager;

    @Resource
    @Lazy
    private DataTypeClassLoader dataTypeClassLoader;

    @Resource
    @Lazy
    private GroovyCodeUtils groovyCodeUtils;

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

    /**
     * 开始同步
     */
    public void startSynchronize() {
        if (hasStart.compareAndSet(false, true)) {
            doSynchronize();
        }
    }

    /**
     * 同步数据源
     */
    @SuppressWarnings("unchecked")
    private void doSynchronize() {
        log.info("Start synchronize node, {}", updateTime.get().format(YYYY_MM_DD_HH_MM_SS));
        NodeExecContext context = new NodeExecContext("NodeManagerSynchronize");
        context.setTransaction(false);
        this.getMetaSubFlox(META_SUB_FLOX_CODE_CONCAT_NODE_FLOX_ENDPOINT).exec(Mono.just(context), Mono.just(Map.of(Constant.UPDATE_TIME, updateTime.get()))).subscribe(l -> {
            try {
                Map<String, List<Map<String, Object>>> res = (Map<String, List<Map<String, Object>>>) l;
                List<Map<String, Object>> nodeRelation = res.get(Constant.NODE_RELATION);
                List<Map<String, Object>> node = res.get(Constant.NODE);
                List<Map<String, Object>> requestExtractor = node.stream().filter(m -> NodeType.REQUEST_EXTRACTOR.getCode().equals(m.get(Constant.TYPE).toString())).toList();
                List<Map<String, Object>> responseLoader = node.stream().filter(m -> NodeType.RESPONSE_LOADER.getCode().equals(m.get(Constant.TYPE).toString())).toList();
                List<Map<String, Object>> subFlox = node.stream().filter(m -> NodeType.SUB_FLOX.getCode().equals(m.get(Constant.TYPE).toString())).toList();
                List<Map<String, Object>> flox = res.get(Constant.FLOX);
                List<Map<String, Object>> endpoint = res.get(Constant.ENDPOINT);

                Optional<OffsetDateTime> updateTimeOption = Stream.concat(Stream.concat(node.stream(), nodeRelation.stream()), Stream.concat(flox.stream(), endpoint.stream()))
                        .filter(m -> Objects.nonNull(m.get(Constant._UPDATE_TIME))).map(m -> (OffsetDateTime) m.get(Constant._UPDATE_TIME)).max(OffsetDateTime::compareTo);

                node = node.stream().filter(m -> {
                    String type = m.get(Constant.TYPE).toString();
                    return !NodeType.SUB_FLOX.getCode().equals(type) && !NodeType.REQUEST_EXTRACTOR.getCode().equals(type) && !NodeType.RESPONSE_LOADER.getCode().equals(type);
                }).toList();

                // 处理 Node Relation
                // 1. 将失效的 node 关系从原 node 关系中删除
                Map<String, List<String>> invalidNodeRelation = getInvalidStream(nodeRelation.stream()).collect(Collectors.groupingBy(
                        m -> m.get(Constant.CODE).toString(),
                        Collectors.mapping(m -> m.get(Constant._SUB_FLOX_CODE).toString(), Collectors.toList())));
                Map<String, Map<String, List<String>>> nodeRelationRes = new HashMap<>(this.nodeRelatedSubFlox);
                for (Map.Entry<String, List<String>> entry : invalidNodeRelation.entrySet()) {
                    String invalidNodeCode = entry.getKey();
                    Map<String, List<String>> originSubFloxCodeMap = nodeRelationRes.get(invalidNodeCode);
                    if (!CollectionUtils.isEmpty(originSubFloxCodeMap)) {
                        for (String invalidSubFloxCode : entry.getValue()) {
                            originSubFloxCodeMap.remove(invalidSubFloxCode);
                        }
                    }
                }
                // 2. 加入新的有效 node 关系
                Map<String, Map<String, List<String>>> validNodeRelation = getValidStream(nodeRelation.stream()).collect(Collectors.groupingBy(
                        m -> m.get(Constant.CODE).toString(),
                        Collectors.mapping(m -> m, Collectors.toMap(m -> (String) m.get(Constant._SUB_FLOX_CODE), m -> Arrays.stream(m.get(Constant._PRE_NODE_CODE_LIST).toString().split(",")).toList()))));
                nodeRelationRes.putAll(validNodeRelation);
                // 3. 计算因为 node 关系改变需要重新创建的子流程
                Set<String> refreshSubFloxCode = Stream.concat(validNodeRelation.values().stream().flatMap(m -> m.keySet().stream()), invalidNodeRelation.values().stream().flatMap(Collection::stream)).collect(Collectors.toSet());

                // 处理 Node
                // 1.将失效的的 node 从原来的 node 中删除
                Set<String> invalidNodeCode = getCodeStream(getInvalidStream(node.stream())).collect(Collectors.toSet());
                Map<String, NodeEntity> nodeRes = new HashMap<>(this.nodeMap);
                for (String invalidCode : invalidNodeCode) {
                    nodeRes.remove(invalidCode);
                }
                // 2.加入新的 node
                List<NodeEntity> validNode = getValidStream(node.stream()).map(m -> buildNodeEntity(m, nodeRelationRes)).toList();
                for (NodeEntity n : validNode) {
                    nodeRes.put(n.nodeCode(), n);
                }
                // 3. 计算因为 node 改变需要重新创建的子流程
                refreshSubFloxCode.addAll(invalidNodeCode);
                refreshSubFloxCode.addAll(validNode.stream().map(NodeEntity::nodeCode).collect(Collectors.toSet()));

                // 处理 子流程
                Map<String, NodeEntity> subFloxRes = new HashMap<>(this.subFloxMap);
                // 1. 用户更新的子流程节点必须包含在目前已有的子流程中
                refreshSubFloxCode.removeAll(getCodeStream(getValidStream(subFlox.stream())).collect(Collectors.toSet()));
                Set<String> unknownRefreshSubFloxCode = new HashSet<>(refreshSubFloxCode);
                unknownRefreshSubFloxCode.removeAll(this.subFloxMap.keySet());
                if (!CollectionUtils.isEmpty(unknownRefreshSubFloxCode)) {
                    log.error("Exist unknown sub flox:[{}]", Arrays.toString(unknownRefreshSubFloxCode.toArray()));
                }
                refreshSubFloxCode.removeAll(unknownRefreshSubFloxCode);
                // 1.1 删除失效的子流程，包括数据库中失效的和用户修改需要刷新的
                Set<String> invalidSubFloxCode = getCodeStream(getInvalidStream(subFlox.stream())).collect(Collectors.toSet());
                refreshSubFloxCode.removeAll(invalidSubFloxCode);
                for (String subFloxCode : invalidSubFloxCode) {
                    subFloxRes.remove(subFloxCode);
                }
                for (String subFloxCode : refreshSubFloxCode) {
                    subFloxRes.remove(subFloxCode);
                }
                // 2. 建立新的子流程
                Map<String, Map<String, Object>> validSubFlox = getValidStream(subFlox.stream()).collect(Collectors.toMap(m -> m.get(Constant.CODE).toString(), m -> m));
                buildNewSubFlox(refreshSubFloxCode, validSubFlox, subFloxRes, nodeRelationRes, nodeRes);
                // 3. 计算需要更新的流程
                Set<String> refreshFloxCode = new HashSet<>();
                Stream.concat(invalidSubFloxCode.stream(), Stream.concat(refreshSubFloxCode.stream(), validSubFlox.keySet().stream())).distinct().forEach(subFloxCode ->
                        refreshFloxCode.addAll(this.subFloxRelatedFlox.get(subFloxCode)));

                // 处理请求抽取器
                // 1. 删除失效的请求抽取器
                Map<String, NodeEntity> requestExtractorRes = new HashMap<>(this.requestExtractorMap);
                Set<String> invalidRequestExtractor = getCodeStream(getInvalidStream(requestExtractor.stream())).collect(Collectors.toSet());
                for (String invalidRequestExtractorCode : invalidRequestExtractor) {
                    requestExtractorRes.remove(invalidRequestExtractorCode);
                }
                // 2. 增加新的请求抽取器
                List<NodeEntity> validRequestExtractor = getValidStream(requestExtractor.stream()).map(m -> buildNodeEntity(m, nodeRelationRes)).toList();
                for (NodeEntity n : validRequestExtractor) {
                    requestExtractorRes.put(n.nodeCode(), n);
                }
                // 3. 计算需要更新的流程
                Stream.concat(invalidRequestExtractor.stream(), validRequestExtractor.stream().map(NodeEntity::nodeCode)).distinct().forEach(requestExtractorCode ->
                        refreshFloxCode.addAll(this.requestExtractorRelatedFlox.get(requestExtractorCode)));

                // 处理响应加载器
                // 1. 删除失效的响应加载器
                Map<String, NodeEntity> responseLoaderRes = new HashMap<>(this.responseLoaderMap);
                Set<String> invalidResponseLoader = getCodeStream(getInvalidStream(responseLoader.stream())).collect(Collectors.toSet());
                for (String invalidResponseLoaderCode : invalidResponseLoader) {
                    responseLoaderRes.remove(invalidResponseLoaderCode);
                }
                // 2. 增加新的响应加载器
                List<NodeEntity> validResponseLoader = getValidStream(requestExtractor.stream()).map(m -> buildNodeEntity(m, nodeRelationRes)).toList();
                for (NodeEntity n : validResponseLoader) {
                    responseLoaderRes.put(n.nodeCode(), n);
                }
                // 3. 计算需要更新的流程
                Stream.concat(invalidResponseLoader.stream(), validResponseLoader.stream().map(NodeEntity::nodeCode)).distinct().forEach(responseLoaderCode ->
                        refreshFloxCode.addAll(this.responseLoaderRelatedFlox.get(responseLoaderCode)));

                // 处理流程
                Map<String, FloxEntity> floxRes = new HashMap<>(this.floxMap);
                // 1. 用户更新的流程必须包含在已有流程中
                refreshFloxCode.removeAll(getCodeStream(getValidStream(flox.stream())).collect(Collectors.toSet()));
                Set<String> unknownRefreshFloxCode = new HashSet<>(refreshFloxCode);
                unknownRefreshFloxCode.removeAll(this.floxMap.keySet());
                if (!CollectionUtils.isEmpty(unknownRefreshFloxCode)) {
                    log.error("Exist unknown flox:[{}]", Arrays.toString(unknownRefreshFloxCode.toArray()));
                }
                refreshFloxCode.removeAll(unknownRefreshFloxCode);
                // 1.1 删除失效的流程
                Set<String> invalidFlox = getCodeStream(getInvalidStream(flox.stream())).collect(Collectors.toSet());
                refreshFloxCode.removeAll(invalidFlox);
                for (String floxCode : invalidFlox) {
                    floxRes.remove(floxCode);
                }
                for (String floxCode : refreshFloxCode) {
                    floxRes.remove(floxCode);
                }
                // 2. 建立新的流程
                Map<String, Map<String, Object>> validFlox = getValidStream(flox.stream()).collect(Collectors.toMap(m -> m.get(Constant.CODE).toString(), m -> m));
                List<FloxEntity> newFlox = validFlox.values().stream().map(m -> {
                    String code = m.get(Constant.CODE).toString();
                    String requestExtractorCode = m.get(Constant._REQUEST_EXTRACTOR_CODE).toString();
                    String responseLoaderCode = m.get(Constant._RESPONSE_LOADER_CODE).toString();
                    String subFloxCode = m.get(Constant._SUB_FLOX_CODE).toString();
                    return new FloxEntity(code, requestExtractorRes.get(requestExtractorCode), subFloxRes.get(subFloxCode), responseLoaderRes.get(responseLoaderCode));
                }).toList();
                List<FloxEntity> freshFlox = refreshFloxCode.stream().map(code -> {
                    FloxEntity old = AssertUtils.assertNonNull(this.floxMap.get(code), "Cannot find old flox :[" + code + "]");
                    return new FloxEntity(code, requestExtractorRes.get(old.requestExtractor().nodeCode()), subFloxRes.get(old.subFlox().nodeCode()), responseLoaderRes.get(old.responseLoader().nodeCode()));
                }).toList();
                Stream.concat(newFlox.stream(), freshFlox.stream()).forEach(f -> floxRes.put(f.code(), f));
                // 3. 计算各种相关的map
                Map<String, List<String>> requestExtractorRelatedFloxRes = floxRes.values().stream().collect(Collectors.groupingBy(f -> f.requestExtractor().nodeCode(), Collectors.mapping(FloxEntity::code, Collectors.toList())));
                Map<String, List<String>> responseLoaderRelatedFloxRes = floxRes.values().stream().collect(Collectors.groupingBy(f -> f.responseLoader().nodeCode(), Collectors.mapping(FloxEntity::code, Collectors.toList())));
                Map<String, List<String>> subFloxRelatedFloxRes = floxRes.values().stream().collect(Collectors.groupingBy(f -> f.subFlox().nodeCode(), Collectors.mapping(FloxEntity::code, Collectors.toList())));
                // 4. 计算需要更新的端点
                Set<String> refreshEndpointCode = new HashSet<>();
                Stream.concat(newFlox.stream().map(FloxEntity::code), freshFlox.stream().map(FloxEntity::code)).distinct().forEach(floxCode ->
                        refreshEndpointCode.addAll(this.floxRelatedEndPoint.get(floxCode)));

                // 处理端点
                Map<String, EndPointEntity> endPointRes = new HashMap<>(this.endPointMap);
                // 1 用户更新的端点必须包含在旧端点
                refreshEndpointCode.removeAll(getCodeStream(getValidStream(endpoint.stream())).collect(Collectors.toSet()));
                Set<String> unknownRefreshEndpointCode = new HashSet<>(refreshEndpointCode);
                unknownRefreshEndpointCode.removeAll(this.endPointMap.keySet());
                if (!CollectionUtils.isEmpty(unknownRefreshEndpointCode)) {
                    log.error("Exist unknown endpoint:[{}]", Arrays.toString(unknownRefreshEndpointCode.toArray()));
                }
                refreshFloxCode.removeAll(unknownRefreshFloxCode);
                // 1.1 处理失效的端点
                Set<String> invalidEndpoint = getCodeStream(getInvalidStream(endpoint.stream())).collect(Collectors.toSet());
                refreshEndpointCode.removeAll(invalidEndpoint);
                for (String endpointCode : invalidEndpoint) {
                    EndPointEntity e = endPointRes.remove(endpointCode);
                    apiManager.deleteHandler(e.path(), e.method());
                }
                // 2. 计算新的端点
                Map<String, Map<String, Object>> validEndpoint = getValidStream(endpoint.stream()).collect(Collectors.toMap(m -> m.get(Constant.CODE).toString(), m -> m));
                List<EndPointEntity> newEndpoint = validEndpoint.values().stream().map(m -> {
                    String code = m.get(Constant.CODE).toString();
                    String path = m.get(Constant.PATH).toString();
                    ApiMethod apiMethod = ApiMethod.fromCode(m.get(Constant.METHOD).toString());
                    String floxCode = m.get(Constant._FLOX_CODE).toString();
                    return new EndPointEntity(code, path, apiMethod, floxCode);
                }).toList();
                newEndpoint.forEach(e -> apiManager.insertHandler(new ApiEndPoint(e.path(), e.method(), new FloxBuilder(floxRes.get(e.floxCode())).build())));
                Stream.concat(newEndpoint.stream(), refreshEndpointCode.stream().map(c -> this.endPointMap.get(c))).forEach(e -> endPointRes.put(e.code(), e));
                refreshEndpointCode.forEach(c -> {
                    EndPointEntity e = endPointRes.get(c);
                    apiManager.updateHandler(new ApiEndPoint(e.path(), e.method(), new FloxBuilder(floxRes.get(e.floxCode())).build()));
                });
                Map<String, List<String>> floxRelatedEndPointRes = endPointRes.values().stream().collect(Collectors.groupingBy(EndPointEntity::floxCode, Collectors.mapping(EndPointEntity::code, Collectors.toList())));

                // 附值结果
                this.nodeRelatedSubFlox = Collections.unmodifiableMap(nodeRelationRes);
                this.nodeMap = Collections.unmodifiableMap(nodeRes);
                this.subFloxMap = Collections.unmodifiableMap(subFloxRes);
                this.requestExtractorMap = Collections.unmodifiableMap(requestExtractorRes);
                this.responseLoaderMap = Collections.unmodifiableMap(responseLoaderRes);
                this.floxMap = Collections.unmodifiableMap(floxRes);
                this.requestExtractorRelatedFlox = Collections.unmodifiableMap(requestExtractorRelatedFloxRes);
                this.responseLoaderRelatedFlox = Collections.unmodifiableMap(responseLoaderRelatedFloxRes);
                this.subFloxRelatedFlox = Collections.unmodifiableMap(subFloxRelatedFloxRes);
                this.endPointMap = Collections.unmodifiableMap(endPointRes);
                this.floxRelatedEndPoint = Collections.unmodifiableMap(floxRelatedEndPointRes);

                // 计算最大updateTime
                this.updateTime.updateAndGet(updateTimeOption::orElse);
            } catch (Exception e) {
                log.error("Synchronize node error : ", e);
            } finally {
                Mono.delay(Duration.ofSeconds(10)).subscribe(i -> doSynchronize());
            }
        });
    }

    /**
     * @param stream stream
     * @return 失效stream
     */
    private Stream<Map<String, Object>> getInvalidStream(Stream<Map<String, Object>> stream) {
        return stream.filter(m -> !Boolean.parseBoolean((String) m.get(Constant.STATUS)));
    }

    /**
     * @param stream stream
     * @return 有效stream
     */
    private Stream<Map<String, Object>> getValidStream(Stream<Map<String, Object>> stream) {
        return stream.filter(m -> Boolean.parseBoolean((String) m.get(Constant.STATUS)));
    }

    /**
     * @param stream stream
     * @return code stream
     */
    private Stream<String> getCodeStream(Stream<Map<String, Object>> stream) {
        return stream.map(m -> (String) m.get(Constant.CODE));
    }

    /**
     * @param map          map
     * @param nodeRelation 节点关系
     * @return node Entity
     */
    private NodeEntity buildNodeEntity(Map<String, Object> map, Map<String, Map<String, List<String>>> nodeRelation) {
        String code = map.get(Constant.CODE).toString();
        NodeType type = NodeType.fromCode(map.get(Constant.TYPE).toString());
        Node node = groovyCodeUtils.getGroovyObject(map.get(Constant.CONTENT).toString(), type.getClazz());
        return buildNodeEntity(code, type, node, map, nodeRelation);
    }

    /**
     * @param code         code
     * @param type         type
     * @param node         node
     * @param map          map
     * @param nodeRelation 节点关系
     * @return node Entity
     */
    private NodeEntity buildNodeEntity(String code, NodeType type, Node node, Map<String, Object> map, Map<String, Map<String, List<String>>> nodeRelation) {
        Map<String, Object> attribute = Optional.ofNullable(GsonUtils.INS.fromJson(map.getOrDefault(Constant.ATTRIBUTE, "").toString(), new TypeToken<Map<String, Object>>() {
        })).orElseGet(() -> HashMap.newHashMap(0));
        List<Class<?>> paramClassList = new LinkedList<>();
        List<String> paramClassStringList = Arrays.stream(map.get(Constant._PARAM_CLASS_LIST).toString().split(",")).toList();
        for (String paramClass : paramClassStringList) {
            try {
                paramClassList.add(dataTypeClassLoader.loadClass(paramClass));
            } catch (ClassNotFoundException e) {
                log.error("Class [" + paramClass + "] not found", e);
            }
        }
        Class<?> resultClass = null;
        String resultClassString = map.get(Constant._RESULT_CLASS).toString();
        try {
            resultClass = dataTypeClassLoader.loadClass(resultClassString);
        } catch (ClassNotFoundException e) {
            log.error("Class [" + resultClassString + "] not found", e);
        }
        return new NodeEntity(code, type, node, attribute, paramClassList, resultClass, nodeRelation.get(code));
    }

    /**
     * @param refreshSubFloxCode 需要刷新的子流程
     * @param validSubFlox       新的 子流程
     * @param subFloxRes         结果
     * @param nodeRelationRes    节点之间的关联关系（新的）
     */
    private void buildNewSubFlox(Set<String> refreshSubFloxCode, Map<String, Map<String, Object>> validSubFlox,
                                 Map<String, NodeEntity> subFloxRes,
                                 Map<String, Map<String, List<String>>> nodeRelationRes,
                                 Map<String, NodeEntity> nodeRes) {
        Set<String> allNewSubFloxCode = new HashSet<>(refreshSubFloxCode);
        allNewSubFloxCode.addAll(validSubFlox.keySet());

        Set<String> allDoneNodeCode = new HashSet<>(nodeRes.keySet());
        allDoneNodeCode.addAll(subFloxRes.keySet());

        Map<String, Set<String>> subFloxRelationNodeList = getSubFloxRelationNodeList(allNewSubFloxCode, nodeRelationRes);
        Stack<String> stack = new Stack<>();
        while (!CollectionUtils.isEmpty(allNewSubFloxCode) || !CollectionUtils.isEmpty(stack)) {
            if (stack.isEmpty()) {
                stack.push(allNewSubFloxCode.stream().findFirst().get());
            }
            Set<String> preNodeSet = subFloxRelationNodeList.get(stack.peek());
            preNodeSet.removeAll(allDoneNodeCode);
            if (CollectionUtils.isEmpty(preNodeSet)) {
                NodeEntity subFlox = buildSubFlox(stack.peek(), validSubFlox, subFloxRes, nodeRes, subFloxRelationNodeList, nodeRelationRes);
                allDoneNodeCode.add(subFlox.nodeCode());
                subFloxRes.put(subFlox.nodeCode(), subFlox);
                allNewSubFloxCode.remove(stack.pop());
            } else {
                Set<String> temp = new HashSet<>(preNodeSet);
                temp.removeAll(allNewSubFloxCode);
                if (!CollectionUtils.isEmpty(temp) || preNodeSet.contains(stack.peek())) {
                    log.error("SubFlox + [" + stack.peek() + "] cannot meet pre node dependency, skip!");
                    allNewSubFloxCode.remove(stack.pop());
                } else {
                    for (String preNode : preNodeSet) {
                        stack.remove(preNode);
                        stack.push(preNode);
                    }
                }
            }
        }
    }

    /**
     * @param code                    code
     * @param validSubFlox            新的子流程
     * @param subFloxRes              目前已经构建的子流程
     * @param nodeRes                 目前已经构建的节点
     * @param subFloxRelationNodeList 子流程依赖的节点
     * @return 子流程
     */
    private NodeEntity buildSubFlox(String code, Map<String, Map<String, Object>> validSubFlox,
                                    Map<String, NodeEntity> subFloxRes,
                                    Map<String, NodeEntity> nodeRes,
                                    Map<String, Set<String>> subFloxRelationNodeList,
                                    Map<String, Map<String, List<String>>> nodeRelationRes) {
        List<NodeEntity> subNodes = new ArrayList<>();
        for (String subNodeCode : subFloxRelationNodeList.getOrDefault(code, new HashSet<>())) {
            subNodes.add(nodeRes.getOrDefault(subNodeCode, subFloxRes.get(subNodeCode)));
        }
        Node node = new DefaultSubFlox(code, subNodes, dataSourceManager);
        if (validSubFlox.containsKey(code)) {
            Map<String, Object> map = validSubFlox.get(code);
            NodeType type = NodeType.fromCode(map.get(Constant.TYPE).toString());
            return buildNodeEntity(code, type, node, map, nodeRelationRes);
        } else {
            NodeEntity originNode = AssertUtils.assertNonNull(this.subFloxMap.get(code), "Sub flox [" + code + "] cannot be found!");
            return new NodeEntity(code, originNode.nodeType(), node, originNode.attribute(), originNode.paramClassList(), originNode.resultClass(), nodeRelationRes.get(code));
        }
    }

    /**
     * @param allNewSubFloxCode 需要获取相关node的子流程code
     * @return 每个子流程的相关
     */
    private Map<String, Set<String>> getSubFloxRelationNodeList(Set<String> allNewSubFloxCode, Map<String, Map<String, List<String>>> nodeRelationRes) {
        Map<String, Set<String>> res = HashMap.newHashMap(allNewSubFloxCode.size());
        for (Map.Entry<String, Map<String, List<String>>> entry : nodeRelationRes.entrySet()) {
            String nodeCode = entry.getKey();
            for (String subFloxCode : entry.getValue().keySet()) {
                if (allNewSubFloxCode.contains(subFloxCode)) {
                    Set<String> nodeCodeList = res.get(subFloxCode);
                    if (CollectionUtils.isEmpty(nodeCodeList)) {
                        nodeCodeList = new HashSet<>();
                        res.put(subFloxCode, nodeCodeList);
                    }
                    nodeCodeList.add(nodeCode);
                }
            }
        }
        List<String> path = new LinkedList<>();
        Set<String> dfsDone = new HashSet<>();
        for (String key : res.keySet()) {
            if (dfsDone.contains(key)) {
                continue;
            }
            dfs(key, path, dfsDone, res);
        }
        return res;
    }

    /**
     * @param key  起点
     * @param path 路径
     * @param res  映射关系
     */
    private void dfs(String key, List<String> path, Set<String> dfsDone, Map<String, Set<String>> res) {
        if (path.contains(key)) {
            throw new RuntimeException("Sub Flox [" + key + "] has circle");
        }
        path.add(key);
        dfsDone.add(key);
        for (String nextKey : res.getOrDefault(key, new HashSet<>())) {
            dfs(nextKey, path, dfsDone, res);
        }
        path.remove(key);
    }
}
