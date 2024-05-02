package com.cc.flox.initializer.meta;

import com.cc.flox.domain.subFlox.impl.DefaultSubFlox;
import com.cc.flox.meta.entity.DataSourcesEntity;
import com.cc.flox.node.NodeManager;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.cc.flox.initializer.meta.MetaNodeInitializer.*;


/**
 * 元子流程初始化器
 *
 * @author cc
 * @date 2024/4/2
 */
@Order(3)
@Component
public class MetaSubFloxInitializer implements CommandLineRunner {

    public static final String META_SUB_FLOX_CODE_ECHO = "meta_sub_echo";

    // data source
    public static final String META_SUB_FLOX_CODE_INSERT_DATA_SOURCE = "meta_sub_insert_data_source";
    public static final String META_SUB_FLOX_CODE_UPDATE_DATA_SOURCE = "meta_sub_update_data_source";
    public static final String META_SUB_FLOX_CODE_SELECT_DATA_SOURCE = "meta_sub_select_data_source";

    // data source action
    public static final String META_SUB_FLOX_CODE_INSERT_DATA_SOURCE_ACTION = "meta_sub_insert_data_source_action";
    public static final String META_SUB_FLOX_CODE_UPDATE_DATA_SOURCE_ACTION = "meta_sub_update_data_source_action";
    public static final String META_SUB_FLOX_CODE_SELECT_DATA_SOURCE_ACTION = "meta_sub_select_data_source_action";
    public static final String META_SUB_FLOX_CODE_CONCAT_DATA_SOURCE_AND_ACTION = "meta_sub_concat_data_source_and_action";

    // data type
    public static final String META_SUB_FLOX_CODE_INSERT_DATA_TYPE = "meta_sub_insert_data_type";
    public static final String META_SUB_FLOX_CODE_SELECT_DATA_TYPE = "meta_sub_select_data_type";

    // node
    public static final String META_SUB_FLOX_CODE_INSERT_NODE = "meta_sub_insert_node";
    public static final String META_SUB_FLOX_CODE_UPDATE_NODE = "meta_sub_update_node";
    public static final String META_SUB_FLOX_CODE_SELECT_NODE = "meta_sub_select_node";

    // node relation
    public static final String META_SUB_FLOX_CODE_INSERT_NODE_RELATION = "meta_sub_insert_node_relation";
    public static final String META_SUB_FLOX_CODE_UPDATE_NODE_RELATION = "meta_sub_update_node_relation";
    public static final String META_SUB_FLOX_CODE_SELECT_NODE_RELATION = "meta_sub_select_node_relation";


    // flox
    public static final String META_SUB_FLOX_CODE_INSERT_FLOX = "meta_sub_insert_flox";
    public static final String META_SUB_FLOX_CODE_UPDATE_FLOX = "meta_sub_update_flox";
    public static final String META_SUB_FLOX_CODE_SELECT_FLOX = "meta_sub_select_flox";

    // endpoint
    public static final String META_SUB_FLOX_CODE_INSERT_ENDPOINT = "meta_sub_insert_endpoint";
    public static final String META_SUB_FLOX_CODE_UPDATE_ENDPOINT = "meta_sub_update_endpoint";
    public static final String META_SUB_FLOX_CODE_SELECT_ENDPOINT = "meta_sub_select_endpoint";

    // node manager
    public static final String META_SUB_FLOX_CODE_CONCAT_NODE_FLOX_ENDPOINT = "meta_sub_concat_node_flox_endpoint";

    @Resource
    private NodeManager nodeManager;

    @Override
    public void run(String... args) {
        nodeManager.putMetaSubFlox(
                META_SUB_FLOX_CODE_ECHO,
                List.of(Map.class),
                Map.class,
                Map.of(META_NODE_CODE_IDENTIFY, List.of(DefaultSubFlox.PRE_NODE_CODE_PARAM))
        );

        // data source
        putBaseInsertMetaSubFlox(META_SUB_FLOX_CODE_INSERT_DATA_SOURCE, META_NODE_CODE_INSERT_DATA_SOURCE);
        putBaseUpdateMetaSubFlox(META_SUB_FLOX_CODE_UPDATE_DATA_SOURCE, META_NODE_CODE_UPDATE_DATA_SOURCE);
        putBaseSelectMetaSubFlox(META_SUB_FLOX_CODE_SELECT_DATA_SOURCE, META_NODE_CODE_SELECT_DATA_SOURCE);

        // data source action
        nodeManager.putMetaSubFlox(
                META_SUB_FLOX_CODE_INSERT_DATA_SOURCE_ACTION,
                List.of(List.class),
                List.class,
                Map.of(META_NODE_CODE_INSERT_DATA_SOURCE_ACTION, List.of(DefaultSubFlox.PRE_NODE_CODE_PARAM, DefaultSubFlox.PRE_NODE_CODE_DATA_SOURCE_MANAGER),
                        META_NODE_CODE_TRANS_DATA_SOURCE_CODE_LIST_TO_CODE_LIST, List.of(DefaultSubFlox.PRE_NODE_CODE_PARAM, META_NODE_CODE_INSERT_DATA_SOURCE_ACTION),
                        META_NODE_CODE_UPDATE_DATA_SOURCE_UPDATE_TIME_BATCH, List.of(META_NODE_CODE_TRANS_DATA_SOURCE_CODE_LIST_TO_CODE_LIST, DefaultSubFlox.PRE_NODE_CODE_DATA_SOURCE_MANAGER))
        );

        nodeManager.putMetaSubFlox(
                META_SUB_FLOX_CODE_UPDATE_DATA_SOURCE_ACTION,
                List.of(Map.class),
                List.class,
                Map.of(META_NODE_CODE_UPDATE_DATA_SOURCE_ACTION, List.of(DefaultSubFlox.PRE_NODE_CODE_PARAM, DefaultSubFlox.PRE_NODE_CODE_DATA_SOURCE_MANAGER),
                        META_NODE_CODE_TRANS_DATA_SOURCE_CODE_MAP_TO_CODE_LIST, List.of(DefaultSubFlox.PRE_NODE_CODE_PARAM, META_NODE_CODE_UPDATE_DATA_SOURCE_ACTION),
                        META_NODE_CODE_UPDATE_DATA_SOURCE_UPDATE_TIME_BATCH, List.of(META_NODE_CODE_TRANS_DATA_SOURCE_CODE_MAP_TO_CODE_LIST, DefaultSubFlox.PRE_NODE_CODE_DATA_SOURCE_MANAGER))
        );

        putBaseSelectMetaSubFlox(META_SUB_FLOX_CODE_SELECT_DATA_SOURCE_ACTION, META_NODE_CODE_SELECT_DATA_SOURCE_ACTION);

        nodeManager.putMetaSubFlox(
                META_SUB_FLOX_CODE_CONCAT_DATA_SOURCE_AND_ACTION,
                List.of(Map.class),
                DataSourcesEntity.class,
                Map.of(META_NODE_CODE_SELECT_DATA_SOURCE, List.of(DefaultSubFlox.PRE_NODE_CODE_PARAM, DefaultSubFlox.PRE_NODE_CODE_DATA_SOURCE_MANAGER),
                        META_NODE_CODE_SELECT_DATA_SOURCE_ACTION, List.of(DefaultSubFlox.PRE_NODE_CODE_PARAM, DefaultSubFlox.PRE_NODE_CODE_DATA_SOURCE_MANAGER),
                        META_NODE_CODE_CONCAT_DATA_SOURCE_AND_ACTION, List.of(META_NODE_CODE_SELECT_DATA_SOURCE, META_NODE_CODE_SELECT_DATA_SOURCE_ACTION))
        );

        // data type
        nodeManager.putMetaSubFlox(
                META_SUB_FLOX_CODE_INSERT_DATA_TYPE,
                List.of(List.class),
                List.class,
                Map.of(META_NODE_CODE_INSERT_DATA_TYPE_TRANSFORMER, List.of(DefaultSubFlox.PRE_NODE_CODE_PARAM),
                        META_NODE_CODE_INSERT_DATA_TYPE, List.of(META_NODE_CODE_INSERT_DATA_TYPE_TRANSFORMER, DefaultSubFlox.PRE_NODE_CODE_DATA_SOURCE_MANAGER))
        );

        putBaseSelectMetaSubFlox(META_SUB_FLOX_CODE_SELECT_DATA_TYPE, META_NODE_CODE_SELECT_DATA_TYPE);

        // node
        nodeManager.putMetaSubFlox(
                META_SUB_FLOX_CODE_INSERT_NODE,
                List.of(List.class),
                List.class,
                Map.of(META_NODE_CODE_INSERT_NODE_TRANSFORMER, List.of(DefaultSubFlox.PRE_NODE_CODE_PARAM),
                        META_NODE_CODE_INSERT_NODE, List.of(META_NODE_CODE_INSERT_NODE_TRANSFORMER, DefaultSubFlox.PRE_NODE_CODE_DATA_SOURCE_MANAGER))

        );

        putBaseUpdateMetaSubFlox(META_SUB_FLOX_CODE_UPDATE_NODE, META_NODE_CODE_UPDATE_NODE);
        putBaseSelectMetaSubFlox(META_SUB_FLOX_CODE_SELECT_NODE, META_NODE_CODE_SELECT_NODE);

        // node relation
        putBaseInsertMetaSubFlox(META_SUB_FLOX_CODE_INSERT_NODE_RELATION, META_NODE_CODE_INSERT_NODE_RELATION);
        putBaseUpdateMetaSubFlox(META_SUB_FLOX_CODE_UPDATE_NODE_RELATION, META_NODE_CODE_UPDATE_NODE_RELATION);
        putBaseSelectMetaSubFlox(META_SUB_FLOX_CODE_SELECT_NODE_RELATION, META_NODE_CODE_SELECT_NODE_RELATION);

        // flox
        putBaseInsertMetaSubFlox(META_SUB_FLOX_CODE_INSERT_FLOX, META_NODE_CODE_INSERT_FLOX);
        putBaseUpdateMetaSubFlox(META_SUB_FLOX_CODE_UPDATE_FLOX, META_NODE_CODE_UPDATE_FLOX);
        putBaseSelectMetaSubFlox(META_SUB_FLOX_CODE_SELECT_FLOX, META_NODE_CODE_SELECT_FLOX);

        // endpoint
        putBaseInsertMetaSubFlox(META_SUB_FLOX_CODE_INSERT_ENDPOINT, META_NODE_CODE_INSERT_ENDPOINT);
        putBaseUpdateMetaSubFlox(META_SUB_FLOX_CODE_UPDATE_ENDPOINT, META_NODE_CODE_UPDATE_ENDPOINT);
        putBaseSelectMetaSubFlox(META_SUB_FLOX_CODE_SELECT_ENDPOINT, META_NODE_CODE_SELECT_ENDPOINT);

        // node manager
        nodeManager.putMetaSubFlox(
                META_SUB_FLOX_CODE_CONCAT_NODE_FLOX_ENDPOINT,
                List.of(Map.class),
                Map.class,
                Map.of(META_NODE_CODE_SELECT_NODE, List.of(DefaultSubFlox.PRE_NODE_CODE_PARAM, DefaultSubFlox.PRE_NODE_CODE_DATA_SOURCE_MANAGER),
                        META_NODE_CODE_SELECT_NODE_RELATION, List.of(DefaultSubFlox.PRE_NODE_CODE_PARAM, DefaultSubFlox.PRE_NODE_CODE_DATA_SOURCE_MANAGER),
                        META_NODE_CODE_SELECT_FLOX, List.of(DefaultSubFlox.PRE_NODE_CODE_PARAM, DefaultSubFlox.PRE_NODE_CODE_DATA_SOURCE_MANAGER),
                        META_NODE_CODE_SELECT_ENDPOINT, List.of(DefaultSubFlox.PRE_NODE_CODE_PARAM, DefaultSubFlox.PRE_NODE_CODE_DATA_SOURCE_MANAGER),
                        META_NODE_CODE_CONCAT_NODE_AND_RELATION, List.of(META_NODE_CODE_SELECT_NODE, META_NODE_CODE_SELECT_NODE_RELATION),
                        META_NODE_CODE_CONCAT_FLOX_AND_ENDPOINT, List.of(META_NODE_CODE_SELECT_FLOX, META_NODE_CODE_SELECT_ENDPOINT),
                        META_NODE_CODE_CONCAT_TWO_MAP, List.of(META_NODE_CODE_CONCAT_NODE_AND_RELATION, META_NODE_CODE_CONCAT_FLOX_AND_ENDPOINT))
        );
    }

    /**
     * @param subFloxCode          子流程code
     * @param dataSourceLoaderCode 数据加载节点code
     */
    private void putBaseInsertMetaSubFlox(String subFloxCode, String dataSourceLoaderCode) {
        nodeManager.putMetaSubFlox(
                subFloxCode,
                List.of(List.class),
                List.class,
                Map.of(dataSourceLoaderCode, List.of(DefaultSubFlox.PRE_NODE_CODE_PARAM, DefaultSubFlox.PRE_NODE_CODE_DATA_SOURCE_MANAGER))
        );
    }

    /**
     * @param subFloxCode          子流程code
     * @param dataSourceLoaderCode 数据加载节点code
     */
    private void putBaseUpdateMetaSubFlox(String subFloxCode, String dataSourceLoaderCode) {
        nodeManager.putMetaSubFlox(
                subFloxCode,
                List.of(Map.class),
                List.class,
                Map.of(dataSourceLoaderCode, List.of(DefaultSubFlox.PRE_NODE_CODE_PARAM, DefaultSubFlox.PRE_NODE_CODE_DATA_SOURCE_MANAGER))
        );
    }

    /**
     * @param subFloxCode          子流程code
     * @param dataSourceLoaderCode 数据加载节点code
     */
    private void putBaseSelectMetaSubFlox(String subFloxCode, String dataSourceLoaderCode) {
        nodeManager.putMetaSubFlox(
                subFloxCode,
                List.of(Map.class),
                List.class,
                Map.of(META_NODE_CODE_MULTI_VALUE_MAP_TO_MAP, List.of(DefaultSubFlox.PRE_NODE_CODE_PARAM),
                        dataSourceLoaderCode, List.of(META_NODE_CODE_MULTI_VALUE_MAP_TO_MAP, DefaultSubFlox.PRE_NODE_CODE_DATA_SOURCE_MANAGER))
        );
    }
}
