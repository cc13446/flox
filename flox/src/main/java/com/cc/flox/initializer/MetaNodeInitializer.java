package com.cc.flox.initializer;

import com.cc.flox.dataSource.DataSourceManager;
import com.cc.flox.domain.loader.DataSourceLoader;
import com.cc.flox.domain.node.NodeType;
import com.cc.flox.domain.transformer.Transformer;
import com.cc.flox.meta.config.MetaDataSourceConfig;
import com.cc.flox.meta.entity.NodeEntity;
import com.cc.flox.node.NodeManager;
import com.cc.flox.utils.AssertUtils;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    public static final String META_NODE_CODE_SELECT_DATA_SOURCE = "meta_node_select_data_source";

    public static final String META_NODE_CODE_SELECT_DATA_SOURCE_ACTION = "meta_node_select_data_source_action";

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
                META_NODE_CODE_SELECT_DATA_SOURCE,
                NodeType.DATA_SOURCE_LOADER,
                DATA_SOURCE_LOADER,
                Map.of(DataSourceLoader.DATA_SOURCE_CODE, MetaDataSourceConfig.META_DATA_SOURCE_KEY, DataSourceLoader.ACTION_CODE, "selectDataSource"),
                List.of(Map.class, DataSourceManager.class),
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

    }
}
