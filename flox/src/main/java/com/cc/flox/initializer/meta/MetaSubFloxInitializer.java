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

    public static final String META_SUB_FLOX_CODE_INSERT_DATA_SOURCE = "meta_sub_insert_data_source";
    public static final String META_SUB_FLOX_CODE_UPDATE_DATA_SOURCE = "meta_sub_update_data_source";
    public static final String META_SUB_FLOX_CODE_SELECT_DATA_SOURCE = "meta_sub_select_data_source";

    public static final String META_SUB_FLOX_CODE_INSERT_DATA_SOURCE_ACTION = "meta_sub_insert_data_source_action";
    public static final String META_SUB_FLOX_CODE_UPDATE_DATA_SOURCE_ACTION = "meta_sub_update_data_source_action";
    public static final String META_SUB_FLOX_CODE_SELECT_DATA_SOURCE_ACTION = "meta_sub_select_data_source_action";

    public static final String META_SUB_FLOX_CODE_INSERT_DATA_TYPE = "meta_sub_insert_data_type";
    public static final String META_SUB_FLOX_CODE_SELECT_DATA_TYPE = "meta_sub_select_data_type";

    public static final String META_SUB_FLOX_CODE_CONCAT_DATA_SOURCE_AND_ACTION = "meta_sub_concat_data_source_and_action";

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

        nodeManager.putMetaSubFlox(
                META_SUB_FLOX_CODE_INSERT_DATA_SOURCE,
                List.of(List.class),
                List.class,
                Map.of(META_NODE_CODE_INSERT_DATA_SOURCE, List.of(DefaultSubFlox.PRE_NODE_CODE_PARAM, DefaultSubFlox.PRE_NODE_CODE_DATA_SOURCE_MANAGER))
        );

        nodeManager.putMetaSubFlox(
                META_SUB_FLOX_CODE_UPDATE_DATA_SOURCE,
                List.of(Map.class),
                List.class,
                Map.of(META_NODE_CODE_UPDATE_DATA_SOURCE, List.of(DefaultSubFlox.PRE_NODE_CODE_PARAM, DefaultSubFlox.PRE_NODE_CODE_DATA_SOURCE_MANAGER))
        );

        nodeManager.putMetaSubFlox(
                META_SUB_FLOX_CODE_SELECT_DATA_SOURCE,
                List.of(Map.class),
                List.class,
                Map.of(META_NODE_CODE_MULTI_VALUE_MAP_TO_MAP, List.of(DefaultSubFlox.PRE_NODE_CODE_PARAM),
                        META_NODE_CODE_SELECT_DATA_SOURCE, List.of(META_NODE_CODE_MULTI_VALUE_MAP_TO_MAP, DefaultSubFlox.PRE_NODE_CODE_DATA_SOURCE_MANAGER))
        );

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

        nodeManager.putMetaSubFlox(
                META_SUB_FLOX_CODE_SELECT_DATA_SOURCE_ACTION,
                List.of(Map.class),
                List.class,
                Map.of(META_NODE_CODE_MULTI_VALUE_MAP_TO_MAP, List.of(DefaultSubFlox.PRE_NODE_CODE_PARAM),
                        META_NODE_CODE_SELECT_DATA_SOURCE_ACTION, List.of(META_NODE_CODE_MULTI_VALUE_MAP_TO_MAP, DefaultSubFlox.PRE_NODE_CODE_DATA_SOURCE_MANAGER))
        );

        nodeManager.putMetaSubFlox(
                META_SUB_FLOX_CODE_INSERT_DATA_TYPE,
                List.of(List.class),
                List.class,
                Map.of(META_NODE_CODE_INSERT_DATA_TYPE_TRANSFORMER, List.of(DefaultSubFlox.PRE_NODE_CODE_PARAM),
                        META_NODE_CODE_INSERT_DATA_TYPE, List.of(META_NODE_CODE_INSERT_DATA_TYPE_TRANSFORMER, DefaultSubFlox.PRE_NODE_CODE_DATA_SOURCE_MANAGER))
        );

        nodeManager.putMetaSubFlox(
                META_SUB_FLOX_CODE_SELECT_DATA_TYPE,
                List.of(Map.class),
                List.class,
                Map.of(META_NODE_CODE_MULTI_VALUE_MAP_TO_MAP, List.of(DefaultSubFlox.PRE_NODE_CODE_PARAM),
                        META_NODE_CODE_SELECT_DATA_TYPE, List.of(META_NODE_CODE_MULTI_VALUE_MAP_TO_MAP, DefaultSubFlox.PRE_NODE_CODE_DATA_SOURCE_MANAGER))
        );

        nodeManager.putMetaSubFlox(
                META_SUB_FLOX_CODE_CONCAT_DATA_SOURCE_AND_ACTION,
                List.of(Map.class),
                DataSourcesEntity.class,
                Map.of(META_NODE_CODE_SELECT_DATA_SOURCE, List.of(DefaultSubFlox.PRE_NODE_CODE_PARAM, DefaultSubFlox.PRE_NODE_CODE_DATA_SOURCE_MANAGER),
                        META_NODE_CODE_SELECT_DATA_SOURCE_ACTION, List.of(DefaultSubFlox.PRE_NODE_CODE_PARAM, DefaultSubFlox.PRE_NODE_CODE_DATA_SOURCE_MANAGER),
                        META_NODE_CODE_CONCAT_DATA_SOURCE_AND_ACTION, List.of(META_NODE_CODE_SELECT_DATA_SOURCE, META_NODE_CODE_SELECT_DATA_SOURCE_ACTION))
        );
    }
}
