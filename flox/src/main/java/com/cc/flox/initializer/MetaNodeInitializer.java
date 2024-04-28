package com.cc.flox.initializer;

import com.cc.flox.domain.loader.DataSourceLoader;
import com.cc.flox.domain.node.NodeType;
import com.cc.flox.domain.transformer.Transformer;
import com.cc.flox.meta.config.MetaDataSourceConfig;
import com.cc.flox.meta.entity.NodeEntity;
import com.cc.flox.node.NodeManager;
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

    public static final String META_NODE_CODE_INSERT_DATA_SOURCE = "meta_node_insert_data_source";

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
                META_NODE_CODE_INSERT_DATA_SOURCE,
                NodeType.DATA_SOURCE_LOADER,
                new DataSourceLoader(),
                Map.of(DataSourceLoader.DATA_SOURCE_CODE, MetaDataSourceConfig.META_DATA_SOURCE_KEY, DataSourceLoader.ACTION_CODE, "insertDataSource"),
                List.of(List.class),
                List.class,
                HashMap.newHashMap(1))
        );
    }
}
