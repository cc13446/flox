package com.cc.flox.initializer.meta;

import com.cc.flox.dataSource.DataSourceManager;
import com.cc.flox.domain.loader.DataSourceLoader;
import com.cc.flox.domain.node.NodeType;
import com.cc.flox.domain.transformer.BiTransformer;
import com.cc.flox.domain.transformer.Transformer;
import com.cc.flox.meta.Constant;
import com.cc.flox.meta.config.MetaDataSourceConfig;
import com.cc.flox.meta.entity.DataSourcesEntity;
import com.cc.flox.meta.entity.NodeEntity;
import com.cc.flox.node.NodeManager;
import com.cc.flox.utils.AssertUtils;
import com.cc.flox.utils.GroovyCodeUtils;
import com.cc.flox.utils.JavaCodeUtils;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.cc.flox.dataType.DataTypeClassLoader.DATA_TYPE_PACKAGE_NAME;
import static com.cc.flox.node.NodeManager.DATA_NODE_PACKAGE_NAME;


/**
 * 元流程节点初始化器
 *
 * @author cc
 * @date 2024/4/2
 */
@Order(2)
@Component
public class MetaNodeInitializer implements CommandLineRunner {

    public static final String META_NODE_CODE_IDENTIFY = "meta_node_identify";
    public static final String META_NODE_CODE_MULTI_VALUE_MAP_TO_MAP = "meta_node_multi_value_map_to_map";

    // data source
    public static final String META_NODE_CODE_INSERT_DATA_SOURCE = "meta_node_insert_data_source";
    public static final String META_NODE_CODE_UPDATE_DATA_SOURCE = "meta_node_update_data_source";
    public static final String META_NODE_CODE_UPDATE_DATA_SOURCE_UPDATE_TIME_BATCH = "meta_node_update_data_source_update_time_batch";
    public static final String META_NODE_CODE_SELECT_DATA_SOURCE = "meta_node_select_data_source";

    // data source action
    public static final String META_NODE_CODE_TRANS_DATA_SOURCE_CODE_MAP_TO_CODE_LIST = "meta_node_trans_data_source_code_map_to_code_list";
    public static final String META_NODE_CODE_TRANS_DATA_SOURCE_CODE_LIST_TO_CODE_LIST = "meta_node_trans_data_source_code_list_to_code_list";
    public static final String META_NODE_CODE_INSERT_DATA_SOURCE_ACTION = "meta_node_insert_data_source_action";
    public static final String META_NODE_CODE_UPDATE_DATA_SOURCE_ACTION = "meta_node_update_data_source_action";
    public static final String META_NODE_CODE_SELECT_DATA_SOURCE_ACTION = "meta_node_select_data_source_action";
    public static final String META_NODE_CODE_CONCAT_DATA_SOURCE_AND_ACTION = "meta_node_concat_data_source_and_action";

    // data type
    public static final String META_NODE_CODE_INSERT_DATA_TYPE_TRANSFORMER = "meta_node_insert_data_type_transformer";
    public static final String META_NODE_CODE_INSERT_DATA_TYPE = "meta_node_insert_data_type";
    public static final String META_NODE_CODE_SELECT_DATA_TYPE = "meta_node_select_data_type";

    // node
    public static final String META_NODE_CODE_INSERT_NODE_TRANSFORMER = "meta_node_insert_NODE_transformer";
    public static final String META_NODE_CODE_INSERT_NODE = "meta_node_insert_node";
    public static final String META_NODE_CODE_UPDATE_NODE = "meta_node_update_node";
    public static final String META_NODE_CODE_SELECT_NODE = "meta_node_select_node";

    // node relation
    public static final String META_NODE_CODE_INSERT_NODE_RELATION = "meta_node_insert_node_relation";
    public static final String META_NODE_CODE_UPDATE_NODE_RELATION = "meta_node_update_node_relation";
    public static final String META_NODE_CODE_SELECT_NODE_RELATION = "meta_node_select_node_relation";

    // flox
    public static final String META_NODE_CODE_INSERT_FLOX = "meta_node_insert_flox";
    public static final String META_NODE_CODE_UPDATE_FLOX = "meta_node_update_flox";
    public static final String META_NODE_CODE_SELECT_FLOX = "meta_node_select_flox";

    // endpoint
    public static final String META_NODE_CODE_INSERT_ENDPOINT = "meta_node_insert_endpoint";
    public static final String META_NODE_CODE_UPDATE_ENDPOINT = "meta_node_update_endpoint";
    public static final String META_NODE_CODE_SELECT_ENDPOINT = "meta_node_select_endpoint";

    // node manager
    public static final String META_NODE_CODE_CONCAT_NODE_AND_RELATION = "meta_node_concat_node_and_relation";
    public static final String META_NODE_CODE_CONCAT_FLOX_AND_ENDPOINT = "meta_node_concat_flox_and_endpoint";
    public static final String META_NODE_CODE_CONCAT_TWO_MAP = "meta_node_concat_two_map";


    /**
     * 默认数据源加载器
     */
    private static final DataSourceLoader DATA_SOURCE_LOADER = new DataSourceLoader();

    @Resource
    private NodeManager nodeManager;

    @Resource
    private GroovyCodeUtils groovyCodeUtils;

    @Override
    public void run(String... args) {
        nodeManager.putMetaNode(new NodeEntity(
                META_NODE_CODE_IDENTIFY,
                NodeType.TRANSFORMER,
                (Transformer<Object, Object>) (source, a) -> source,
                List.of(Object.class),
                Object.class)
        );

        nodeManager.putMetaNode(new NodeEntity(
                META_NODE_CODE_MULTI_VALUE_MAP_TO_MAP,
                NodeType.TRANSFORMER,
                (Transformer<Map<String, List<Object>>, Map<String, Object>>) (m, a) -> m.map(map -> {
                    Map<String, Object> res = new HashMap<>();
                    for (Map.Entry<String, List<Object>> entry : map.entrySet()) {
                        AssertUtils.assertTrue(entry.getValue().size() == 1, "Multi map cannot trans to map, because [" + entry.getKey() + "] has more then one value");
                        res.put(entry.getKey(), entry.getValue().getFirst());
                    }
                    return res;
                }),
                HashMap.newHashMap(1),
                List.of(Map.class),
                Map.class,
                HashMap.newHashMap(1))
        );

        // data source
        putBaseInsertMetaNode(META_NODE_CODE_INSERT_DATA_SOURCE, "insertDataSource");
        putBaseUpdateMetaNode(META_NODE_CODE_UPDATE_DATA_SOURCE, "updateDataSource");
        putBaseSelectMetaNode(META_NODE_CODE_SELECT_DATA_SOURCE, "selectDataSource");

        nodeManager.putMetaNode(new NodeEntity(
                META_NODE_CODE_UPDATE_DATA_SOURCE_UPDATE_TIME_BATCH,
                NodeType.DATA_SOURCE_LOADER,
                DATA_SOURCE_LOADER,
                Map.of(DataSourceLoader.DATA_SOURCE_CODE, MetaDataSourceConfig.META_DATA_SOURCE_KEY, DataSourceLoader.ACTION_CODE, "updateDataSourceUpdateTimeBatch"),
                List.of(List.class, DataSourceManager.class),
                List.class,
                HashMap.newHashMap(1))
        );

        // data source action
        nodeManager.putMetaNode(new NodeEntity(
                META_NODE_CODE_TRANS_DATA_SOURCE_CODE_MAP_TO_CODE_LIST,
                NodeType.BI_TRANSFORMER,
                (BiTransformer<Map<String, Object>, Object, List<String>>) (source, o, a) -> Mono.zip(source, o).flatMap(t -> Mono.just(List.of(t.getT1().get(Constant.DATA_SOURCE_CODE).toString()))),
                List.of(Map.class, Object.class),
                List.class)
        );

        nodeManager.putMetaNode(new NodeEntity(
                META_NODE_CODE_TRANS_DATA_SOURCE_CODE_LIST_TO_CODE_LIST,
                NodeType.BI_TRANSFORMER,
                (BiTransformer<List<Map<String, Object>>, Object, List<String>>) (source, o, a) -> Mono.zip(source, o).flatMap(t -> Mono.just(t.getT1().stream().map(m -> m.get(Constant.DATA_SOURCE_CODE).toString()).toList())),
                List.of(List.class, Object.class),
                List.class)
        );

        putBaseInsertMetaNode(META_NODE_CODE_INSERT_DATA_SOURCE_ACTION, "insertDataSourceAction");
        putBaseUpdateMetaNode(META_NODE_CODE_UPDATE_DATA_SOURCE_ACTION, "updateDataSourceAction");
        putBaseSelectMetaNode(META_NODE_CODE_SELECT_DATA_SOURCE_ACTION, "selectDataSourceAction");

        nodeManager.putMetaNode(new NodeEntity(
                META_NODE_CODE_CONCAT_DATA_SOURCE_AND_ACTION,
                NodeType.BI_TRANSFORMER,
                (BiTransformer<List<Map<String, Object>>, List<Map<String, Object>>, DataSourcesEntity>) (dataSource, action, attr) -> Mono.zip(dataSource, action).flatMap(t -> {
                    List<Map<String, Object>> d = t.getT1();
                    List<Map<String, Object>> a = t.getT2();
                    if (CollectionUtils.isEmpty(d)) {
                        return Mono.just(new DataSourcesEntity(List.of(), List.of()));
                    }
                    List<Map<String, Object>> actions = a.stream()
                            .filter(m -> Objects.nonNull(m.get(Constant.CODE))
                                    && Objects.nonNull(m.get(Constant._DATA_SOURCE_CODE))
                                    && Objects.nonNull(m.get(Constant.TYPE))
                                    && Objects.nonNull(m.get(Constant.SQL))
                                    && Objects.nonNull(m.get(Constant.STATUS))).toList();

                    List<Map<String, Object>> dataSources = d.stream()
                            .filter(m -> Objects.nonNull(m.get(Constant.CODE))
                                    && Objects.nonNull(m.get(Constant.TYPE))
                                    && Objects.nonNull(m.get(Constant.URL))
                                    && Objects.nonNull(m.get(Constant.USERNAME))
                                    && Objects.nonNull(m.get(Constant.PASSWORD))
                                    && Objects.nonNull(m.get(Constant.CONFIG))
                                    && Objects.nonNull(m.get(Constant.STATUS))).toList();
                    return Mono.just(new DataSourcesEntity(dataSources, actions));
                }),
                HashMap.newHashMap(1),
                List.of(List.class, List.class),
                DataSourcesEntity.class,
                HashMap.newHashMap(1))
        );

        // data type
        nodeManager.putMetaNode(new NodeEntity(
                META_NODE_CODE_INSERT_DATA_TYPE_TRANSFORMER,
                NodeType.TRANSFORMER,
                (Transformer<List<Map<String, Object>>, List<Map<String, Object>>>) (l, a) -> l.flatMap(list -> Mono.just(list.stream().peek(m -> {
                    AssertUtils.assertNonBlank((String) m.get(Constant.CODE), "Data type code cannot be blank");
                    AssertUtils.assertNonBlank((String) m.get(Constant.CONTENT), "Data type content cannot be blank");
                    String code = m.get(Constant.CONTENT).toString();
                    String expectPackage = DATA_TYPE_PACKAGE_NAME;
                    String packageName = JavaCodeUtils.getPackageNameFromCode(code);
                    AssertUtils.assertTrue(expectPackage.equals(packageName), "The package name of data type content is fixed(" + expectPackage + ")");
                    String dataTypeCode = m.get(Constant.CODE).toString();
                    String expectClassName = dataTypeCode.substring(0, 1).toUpperCase() + dataTypeCode.substring(1);
                    String path = expectPackage + "." + expectClassName;
                    m.put(Constant.PATH, path);
                    String className = JavaCodeUtils.getClassNameFromCode(code);
                    AssertUtils.assertNonBlank(className, "Cannot find data type class name");
                    AssertUtils.assertTrue(expectClassName.equals(className), "Class name should equals code (first char upper)");
                    byte[] bytes = JavaCodeUtils.codeToClass(code);
                    AssertUtils.assertTrue(Objects.nonNull(bytes) && bytes.length != 0, "Content cannot be compile");
                }).toList())),
                HashMap.newHashMap(1),
                List.of(List.class),
                List.class,
                HashMap.newHashMap(1))
        );

        putBaseInsertMetaNode(META_NODE_CODE_INSERT_DATA_TYPE, "insertDataType");
        putBaseSelectMetaNode(META_NODE_CODE_SELECT_DATA_TYPE, "selectDataType");

        // node
        nodeManager.putMetaNode(new NodeEntity(
                META_NODE_CODE_INSERT_NODE_TRANSFORMER,
                NodeType.TRANSFORMER,
                (Transformer<List<Map<String, Object>>, List<Map<String, Object>>>) (l, a) -> l.flatMap(list -> Mono.just(list.stream().peek(m -> {
                    AssertUtils.assertNonBlank((String) m.get(Constant.CODE), "Node code cannot be blank");
                    AssertUtils.assertNonBlank((String) m.get(Constant.CONTENT), "Node content cannot be blank");
                    NodeType type = AssertUtils.assertNonNull(NodeType.fromCode((String) m.get(Constant.TYPE)), "Node type cannot be null");
                    String code = m.get(Constant.CONTENT).toString();
                    AssertUtils.assertTrue(DATA_NODE_PACKAGE_NAME.equals(GroovyCodeUtils.getPackageNameFromCode(code)), "The package name of data type content is fixed(" + DATA_NODE_PACKAGE_NAME + ")");
                    AssertUtils.assertNonNull(groovyCodeUtils.getGroovyObject(code, type.getClazz()), "Node groovy code cannot be compiled");
                }).toList())),
                HashMap.newHashMap(1),
                List.of(List.class),
                List.class,
                HashMap.newHashMap(1))
        );

        putBaseInsertMetaNode(META_NODE_CODE_INSERT_NODE, "insertNode");
        putBaseUpdateMetaNode(META_NODE_CODE_UPDATE_NODE, "updateNode");
        putBaseSelectMetaNode(META_NODE_CODE_SELECT_NODE, "selectNode");

        // node_relation
        putBaseInsertMetaNode(META_NODE_CODE_INSERT_NODE_RELATION, "insertNodeRelation");
        putBaseUpdateMetaNode(META_NODE_CODE_UPDATE_NODE_RELATION, "updateNodeRelation");
        putBaseSelectMetaNode(META_NODE_CODE_SELECT_NODE_RELATION, "selectNodeRelation");

        // flox
        putBaseInsertMetaNode(META_NODE_CODE_INSERT_FLOX, "insertFlox");
        putBaseUpdateMetaNode(META_NODE_CODE_UPDATE_FLOX, "updateFlox");
        putBaseSelectMetaNode(META_NODE_CODE_SELECT_FLOX, "selectFlox");

        // endpoint
        putBaseInsertMetaNode(META_NODE_CODE_INSERT_ENDPOINT, "insertEndpoint");
        putBaseUpdateMetaNode(META_NODE_CODE_UPDATE_ENDPOINT, "updateEndpoint");
        putBaseSelectMetaNode(META_NODE_CODE_SELECT_ENDPOINT, "selectEndpoint");

        // node manager
        nodeManager.putMetaNode(new NodeEntity(
                META_NODE_CODE_CONCAT_NODE_AND_RELATION,
                NodeType.BI_TRANSFORMER,
                (BiTransformer<List<Map<String, Object>>, List<Map<String, Object>>, Map<String, List<Map<String, Object>>>>) (n, r, a) -> Mono.zip(n, r).flatMap(t -> {
                    Map<String, List<Map<String, Object>>> res = HashMap.newHashMap(2);
                    res.put(Constant.NODE, t.getT1());
                    res.put(Constant.NODE_RELATION, t.getT2());
                    return Mono.just(res);
                }),
                HashMap.newHashMap(1),
                List.of(List.class, List.class),
                Map.class,
                HashMap.newHashMap(1))
        );

        nodeManager.putMetaNode(new NodeEntity(
                META_NODE_CODE_CONCAT_FLOX_AND_ENDPOINT,
                NodeType.BI_TRANSFORMER,
                (BiTransformer<List<Map<String, Object>>, List<Map<String, Object>>, Map<String, List<Map<String, Object>>>>) (f, e, a) -> Mono.zip(f, e).flatMap(t -> {
                    Map<String, List<Map<String, Object>>> res = HashMap.newHashMap(2);
                    res.put(Constant.FLOX, t.getT1());
                    res.put(Constant.ENDPOINT, t.getT2());
                    return Mono.just(res);
                }),
                HashMap.newHashMap(1),
                List.of(List.class, List.class),
                Map.class,
                HashMap.newHashMap(1))
        );

        nodeManager.putMetaNode(new NodeEntity(
                META_NODE_CODE_CONCAT_TWO_MAP,
                NodeType.BI_TRANSFORMER,
                (BiTransformer<Map<String, List<Map<String, Object>>>, Map<String, List<Map<String, Object>>>, Map<String, List<Map<String, Object>>>>) (one, two, a) -> Mono.zip(one, two).flatMap(t -> {
                    t.getT1().putAll(t.getT2());
                    return Mono.just(t.getT1());
                }),
                HashMap.newHashMap(1),
                List.of(Map.class, Map.class),
                Map.class,
                HashMap.newHashMap(1))
        );
    }

    /**
     * @param nodeCode   node code
     * @param actionCode action code
     */
    private void putBaseInsertMetaNode(String nodeCode, String actionCode) {
        putBaseDataSourceLoaderNode(nodeCode, actionCode, List.of(List.class, DataSourceManager.class), List.class);
    }

    /**
     * @param nodeCode   node code
     * @param actionCode action code
     */
    private void putBaseUpdateMetaNode(String nodeCode, String actionCode) {
        putBaseDataSourceLoaderNode(nodeCode, actionCode, List.of(Map.class, DataSourceManager.class), List.class);
    }

    /**
     * @param nodeCode   node code
     * @param actionCode action code
     */
    private void putBaseSelectMetaNode(String nodeCode, String actionCode) {
        putBaseDataSourceLoaderNode(nodeCode, actionCode, List.of(Map.class, DataSourceManager.class), List.class);
    }

    /**
     * @param nodeCode       node code
     * @param actionCode     action code
     * @param paramClassList 参数类型
     * @param resultClass    结果类型
     */
    private void putBaseDataSourceLoaderNode(String nodeCode, String actionCode, List<Class<?>> paramClassList, Class<?> resultClass) {
        nodeManager.putMetaNode(new NodeEntity(
                nodeCode,
                NodeType.DATA_SOURCE_LOADER,
                DATA_SOURCE_LOADER,
                Map.of(DataSourceLoader.DATA_SOURCE_CODE, MetaDataSourceConfig.META_DATA_SOURCE_KEY, DataSourceLoader.ACTION_CODE, actionCode),
                paramClassList,
                resultClass,
                HashMap.newHashMap(1))
        );
    }
}
