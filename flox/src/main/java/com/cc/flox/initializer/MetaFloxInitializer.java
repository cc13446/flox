package com.cc.flox.initializer;

import com.cc.flox.api.ApiManager;
import com.cc.flox.api.endpoint.ApiEndPoint;
import com.cc.flox.api.endpoint.ApiMethod;
import com.cc.flox.dataSource.DataSourceManager;
import com.cc.flox.domain.flox.FloxBuilder;
import com.cc.flox.domain.loader.dataSourceLoader.DataSourceLoader;
import com.cc.flox.domain.loader.impl.DefaultResponseLoader;
import com.cc.flox.domain.node.NodeType;
import com.cc.flox.domain.subFlox.SubFlox;
import com.cc.flox.domain.subFlox.impl.DefaultSubFlox;
import com.cc.flox.domain.transformer.Transformer;
import com.cc.flox.meta.config.MetaDataSourceConfig;
import com.cc.flox.meta.entity.NodeEntity;
import com.cc.flox.node.NodeManager;
import com.cc.flox.utils.AssertUtils;
import com.cc.flox.utils.GsonUtils;
import com.google.gson.reflect.TypeToken;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 元流程初始化器
 *
 * @author cc
 * @date 2024/4/2
 */
@Order(2)
@Component
public class MetaFloxInitializer implements CommandLineRunner {

    private static final String SUB_FLOX_CODE_ECHO = "meta_echo";

    private static final String SUB_FLOX_CODE_INSERT_DATA_SOURCE = "meta_insert_data_source";

    @Resource
    private ApiManager apiManager;

    @Resource
    private DataSourceManager dataSourceManager;

    @Resource
    private NodeManager nodeManager;

    @Override
    public void run(String... args) throws Exception {
        apiManager.insertHandler(getEchoEndPoint()).get();
        apiManager.insertHandler(getInsertDataSourceEndPoint()).get();
    }

    /**
     * @return echo end point
     */
    private ApiEndPoint getEchoEndPoint() {
        nodeManager.putMetaNode(new NodeEntity(
                "identify",
                NodeType.TRANSFORMER,
                (Transformer<Map<String, String>, Map<String, String>>) source -> source,
                HashMap.newHashMap(1),
                List.of(Map.class),
                Map.class,
                Map.of(SUB_FLOX_CODE_ECHO, List.of(DefaultSubFlox.PARAM_NODE_CODE)))
        );

        nodeManager.putMetaSubFlox(new NodeEntity(
                SUB_FLOX_CODE_ECHO,
                NodeType.SUB_FLOX,
                new DefaultSubFlox(
                        SUB_FLOX_CODE_ECHO,
                        Map.class,
                        Map.class,
                        nodeManager.getMetaNodeBySubFlox(SUB_FLOX_CODE_ECHO),
                        dataSourceManager),
                HashMap.newHashMap(1),
                List.of(Map.class),
                Map.class,
                HashMap.newHashMap(1))
        );

        FloxBuilder builder = new FloxBuilder()
                .setRequestExtractorBuilder(() -> m -> m.map(ServerHttpRequest::getQueryParams))
                .setSubFloxBuilder(() -> {
                    NodeEntity node = nodeManager.getMetaSubFlox(SUB_FLOX_CODE_ECHO);
                    AssertUtils.assertTrue(node.nodeType() == NodeType.SUB_FLOX, "Flox only accept sub flox, but receive [" + node.nodeType().getCode() + "]");
                    return (SubFlox) node.node();
                })
                .setResponseLoaderBuilder(DefaultResponseLoader::new);
        return new ApiEndPoint("/echo", ApiMethod.GET, builder.builder());
    }

    /**
     * @return insert data source end point
     */
    private ApiEndPoint getInsertDataSourceEndPoint() {

        nodeManager.putMetaNode(new NodeEntity(
                "insertDataSource",
                NodeType.DATA_SOURCE_LOADER,
                new DataSourceLoader(),
                Map.of(DefaultSubFlox.DATA_SOURCE_CODE, MetaDataSourceConfig.META_DATA_SOURCE_KEY, DefaultSubFlox.ACTION_CODE, "insertDataSource"),
                List.of(List.class),
                List.class,
                Map.of(SUB_FLOX_CODE_INSERT_DATA_SOURCE, List.of(DefaultSubFlox.PARAM_NODE_CODE)))
        );

        nodeManager.putMetaSubFlox(new NodeEntity(
                SUB_FLOX_CODE_INSERT_DATA_SOURCE,
                NodeType.SUB_FLOX,
                new DefaultSubFlox(
                        SUB_FLOX_CODE_INSERT_DATA_SOURCE,
                        List.class,
                        List.class,
                        nodeManager.getMetaNodeBySubFlox(SUB_FLOX_CODE_INSERT_DATA_SOURCE),
                        dataSourceManager),
                HashMap.newHashMap(1),
                List.of(Map.class),
                Map.class,
                HashMap.newHashMap(1))
        );

        FloxBuilder builder = new FloxBuilder()
                .setRequestExtractorBuilder(() -> m -> m.flatMap(r -> DataBufferUtils.join(r.getBody())).flatMap(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return Mono.just(GsonUtils.INS.fromJson(new String(bytes, StandardCharsets.UTF_8), new TypeToken<List<Map<String, Object>>>() {
                    }));
                }))
                .setSubFloxBuilder(() -> {
                    NodeEntity node = nodeManager.getMetaSubFlox(SUB_FLOX_CODE_INSERT_DATA_SOURCE);
                    AssertUtils.assertTrue(node.nodeType() == NodeType.SUB_FLOX, "Flox only accept sub flox, but receive [" + node.nodeType().getCode() + "]");
                    return (SubFlox) node.node();
                })
                .setResponseLoaderBuilder(DefaultResponseLoader::new);
        return new ApiEndPoint("/data-source/insert", ApiMethod.POST, builder.builder());
    }
}
