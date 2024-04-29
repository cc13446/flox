package com.cc.flox.initializer;

import com.cc.flox.domain.subFlox.impl.DefaultSubFlox;
import com.cc.flox.node.NodeManager;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.cc.flox.initializer.MetaNodeInitializer.*;


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

    public static final String META_SUB_FLOX_CODE_SELECT_DATA_SOURCE = "meta_sub_select_data_source";

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
                META_SUB_FLOX_CODE_SELECT_DATA_SOURCE,
                List.of(Map.class),
                List.class,
                Map.of(META_NODE_CODE_MULTI_VALUE_MAP_TO_MAP, List.of(DefaultSubFlox.PRE_NODE_CODE_PARAM),
                        META_NODE_CODE_SELECT_DATA_SOURCE, List.of(META_NODE_CODE_MULTI_VALUE_MAP_TO_MAP, DefaultSubFlox.PRE_NODE_CODE_DATA_SOURCE_MANAGER))
        );
    }
}
