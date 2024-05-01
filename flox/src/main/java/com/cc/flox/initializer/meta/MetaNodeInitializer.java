package com.cc.flox.initializer.meta;

import com.cc.flox.dataSource.DataSource;
import com.cc.flox.dataSource.DataSourceConfiguration;
import com.cc.flox.dataSource.DataSourceManager;
import com.cc.flox.dataSource.DataSourceType;
import com.cc.flox.dataSource.action.Action;
import com.cc.flox.dataSource.template.TemplateType;
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
import com.cc.flox.utils.GsonUtils;
import com.google.gson.reflect.TypeToken;
import io.r2dbc.pool.ConnectionPool;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


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

    public static final String META_NODE_CODE_INSERT_DATA_SOURCE = "meta_node_insert_data_source";
    public static final String META_NODE_CODE_UPDATE_DATA_SOURCE = "meta_node_update_data_source";
    public static final String META_NODE_CODE_SELECT_DATA_SOURCE = "meta_node_select_data_source";

    public static final String META_NODE_CODE_INSERT_DATA_SOURCE_ACTION = "meta_node_insert_data_source_action";
    public static final String META_NODE_CODE_SELECT_DATA_SOURCE_ACTION = "meta_node_select_data_source_action";

    public static final String META_NODE_CODE_CONCAT_DATA_SOURCE_AND_ACTION = "meta_node_concat_data_source_and_action";

    /**
     * 默认数据源加载器
     */
    private static final DataSourceLoader DATA_SOURCE_LOADER = new DataSourceLoader();

    @Resource
    private NodeManager nodeManager;

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

        nodeManager.putMetaNode(new NodeEntity(
                META_NODE_CODE_INSERT_DATA_SOURCE,
                NodeType.DATA_SOURCE_LOADER,
                DATA_SOURCE_LOADER,
                Map.of(DataSourceLoader.DATA_SOURCE_CODE, MetaDataSourceConfig.META_DATA_SOURCE_KEY, DataSourceLoader.ACTION_CODE, "insertDataSource"),
                List.of(List.class, DataSourceManager.class),
                List.class,
                HashMap.newHashMap(1))
        );

        nodeManager.putMetaNode(new NodeEntity(
                META_NODE_CODE_UPDATE_DATA_SOURCE,
                NodeType.DATA_SOURCE_LOADER,
                DATA_SOURCE_LOADER,
                Map.of(DataSourceLoader.DATA_SOURCE_CODE, MetaDataSourceConfig.META_DATA_SOURCE_KEY, DataSourceLoader.ACTION_CODE, "updateDataSource"),
                List.of(Map.class, DataSourceManager.class),
                List.class,
                HashMap.newHashMap(1))
        );

        nodeManager.putMetaNode(new NodeEntity(
                META_NODE_CODE_SELECT_DATA_SOURCE,
                NodeType.DATA_SOURCE_LOADER,
                DATA_SOURCE_LOADER,
                Map.of(DataSourceLoader.DATA_SOURCE_CODE, MetaDataSourceConfig.META_DATA_SOURCE_KEY, DataSourceLoader.ACTION_CODE, "selectDataSource"),
                List.of(Map.class, DataSourceManager.class),
                List.class,
                HashMap.newHashMap(1))
        );

        nodeManager.putMetaNode(new NodeEntity(
                META_NODE_CODE_INSERT_DATA_SOURCE_ACTION,
                NodeType.DATA_SOURCE_LOADER,
                DATA_SOURCE_LOADER,
                Map.of(DataSourceLoader.DATA_SOURCE_CODE, MetaDataSourceConfig.META_DATA_SOURCE_KEY, DataSourceLoader.ACTION_CODE, "insertDataSourceAction"),
                List.of(List.class, DataSourceManager.class),
                List.class,
                HashMap.newHashMap(1))
        );

        nodeManager.putMetaNode(new NodeEntity(
                META_NODE_CODE_SELECT_DATA_SOURCE_ACTION,
                NodeType.DATA_SOURCE_LOADER,
                DATA_SOURCE_LOADER,
                Map.of(DataSourceLoader.DATA_SOURCE_CODE, MetaDataSourceConfig.META_DATA_SOURCE_KEY, DataSourceLoader.ACTION_CODE, "selectDataSourceAction"),
                List.of(Map.class, DataSourceManager.class),
                List.class,
                HashMap.newHashMap(1))
        );

        nodeManager.putMetaNode(new NodeEntity(
                META_NODE_CODE_CONCAT_DATA_SOURCE_AND_ACTION,
                NodeType.BI_TRANSFORMER,
                (BiTransformer<List<Map<String, Object>>, List<Map<String, Object>>, DataSourcesEntity>) (dataSource, action, attr) -> Mono.zip(dataSource, action).flatMap(t -> {
                    List<Map<String, Object>> d = t.getT1();
                    List<Map<String, Object>> a = t.getT2();
                    if (CollectionUtils.isEmpty(d)) {
                        return Mono.just(new DataSourcesEntity(List.of(), null));
                    }
                    OffsetDateTime dataSourceUpdateTime = d.stream().filter(m -> Objects.nonNull(m.get(Constant._UPDATE_TIME))).map(m -> (OffsetDateTime) m.get(Constant._UPDATE_TIME)).max(OffsetDateTime::compareTo).orElse(OffsetDateTime.MIN);
                    OffsetDateTime actionSourceUpdateTime = a.stream().filter(m -> Objects.nonNull(m.get(Constant._UPDATE_TIME))).map(m -> (OffsetDateTime) m.get(Constant._UPDATE_TIME)).max(OffsetDateTime::compareTo).orElse(OffsetDateTime.MIN);
                    Map<String, List<Action>> actionMap = a.stream()
                            .filter(m -> Objects.nonNull(m.get(Constant.CODE)) && Objects.nonNull(m.get(Constant._DATA_SOURCE_CODE)) && Objects.nonNull(m.get(Constant.TYPE)) && Objects.nonNull(m.get(Constant.SQL)))
                            .collect(Collectors.groupingBy(m -> m.get(Constant._DATA_SOURCE_CODE).toString(), Collectors.mapping(m -> new Action(
                                    m.get(Constant.CODE).toString(),
                                    AssertUtils.assertNonNull(TemplateType.fromCode(m.get(Constant.TYPE).toString()), "Unknown template type " + m.get(Constant.TYPE)),
                                    m.get(Constant.SQL).toString()
                            ), Collectors.toList())));

                    List<DataSource> res = d.stream().filter(m -> Objects.nonNull(m.get(Constant.CODE)) && Objects.nonNull(m.get(Constant.TYPE)) && Objects.nonNull(m.get(Constant.URL)) && Objects.nonNull(m.get(Constant.USERNAME)) && Objects.nonNull(m.get(Constant.PASSWORD)) && Objects.nonNull(m.get(Constant.CONFIG)))
                            .map(m -> {
                                Map<String, Object> config = GsonUtils.INS.fromJson(m.get(Constant.CONFIG).toString(), new TypeToken<>() {
                                });
                                if (CollectionUtils.isEmpty(config)) {
                                    config = HashMap.newHashMap(0);
                                }
                                return new DataSourceConfiguration(
                                        m.get(Constant.CODE).toString(),
                                        m.get(Constant.URL).toString(),
                                        m.get(Constant.USERNAME).toString(),
                                        m.get(Constant.PASSWORD).toString(),
                                        AssertUtils.assertNonNull(DataSourceType.fromCode(m.get(Constant.TYPE).toString()), "Unknown data source type" + m.get(Constant.TYPE)),
                                        Integer.parseInt(config.getOrDefault(Constant.INIT_SIZE, DataSourceConfiguration.DEFAULT_INIT_SIZE).toString()),
                                        Integer.parseInt(config.getOrDefault(Constant.MAX_SIZE, DataSourceConfiguration.DEFAULT_MAX_SIZE).toString()),
                                        Integer.parseInt(config.getOrDefault(Constant.MAX_IDLE, DataSourceConfiguration.DEFAULT_MAX_IDLE).toString()),
                                        actionMap.getOrDefault(m.get(Constant.CODE).toString(), List.of()).stream().collect(Collectors.toMap(Action::getCode, s -> s)));
                            })
                            .map(c -> new DataSource(
                                    c.code(),
                                    new R2dbcEntityTemplate(new ConnectionPool(c.getConnectionPoolConfiguration())),
                                    c.action(),
                                    c.type())).toList();
                    return Mono.just(new DataSourcesEntity(res, dataSourceUpdateTime.isAfter(actionSourceUpdateTime) ? dataSourceUpdateTime : actionSourceUpdateTime));
                }),
                HashMap.newHashMap(1),
                List.of(List.class, List.class),
                DataSourcesEntity.class,
                HashMap.newHashMap(1))
        );
    }
}
