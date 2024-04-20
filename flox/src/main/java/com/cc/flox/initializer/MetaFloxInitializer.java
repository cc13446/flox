package com.cc.flox.initializer;

import com.cc.flox.api.ApiManager;
import com.cc.flox.api.endpoint.ApiEndPoint;
import com.cc.flox.api.endpoint.ApiMethod;
import com.cc.flox.dataSource.DataSourceManager;
import com.cc.flox.domain.flox.FloxBuilder;
import com.cc.flox.domain.loader.dataSourceLoader.DataSourceLoader;
import com.cc.flox.domain.loader.impl.DefaultResponseLoader;
import com.cc.flox.domain.node.NodeType;
import com.cc.flox.domain.subFlox.impl.DefaultSubFlox;
import com.cc.flox.domain.transformer.Transformer;
import com.cc.flox.meta.config.MetaDataSourceConfig;
import com.cc.flox.meta.entity.NodeEntity;
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
    @Resource
    private ApiManager apiManager;

    @Resource
    private DataSourceManager dataSourceManager;

    @Override
    public void run(String... args) throws Exception {
        apiManager.insertHandler(getEchoEndPoint()).get();
        apiManager.insertHandler(getInsertDataSourceEndPoint()).get();
    }

    /**
     * @return echo end point
     */
    private ApiEndPoint getEchoEndPoint() {
        FloxBuilder builder = new FloxBuilder()
                .setRequestExtractorBuilder(() -> m -> m.map(ServerHttpRequest::getQueryParams))
                .setSubFloxBuilder(() -> new DefaultSubFlox(
                        Map.class,
                        Map.class,
                        List.of(new NodeEntity(
                                "identify",
                                NodeType.TRANSFORMER,
                                (Transformer<String, String>) source -> source,
                                HashMap.newHashMap(1),
                                List.of(Map.class),
                                Map.class,
                                "echo",
                                List.of(DefaultSubFlox.PARAM_NODE_CODE)
                        )),
                        dataSourceManager
                ))
                .setResponseLoaderBuilder(DefaultResponseLoader::new);
        return new ApiEndPoint("/echo", ApiMethod.GET, builder.builder());
    }

    /**
     * @return insert data source end point
     */
    private ApiEndPoint getInsertDataSourceEndPoint() {
        FloxBuilder builder = new FloxBuilder()
                .setRequestExtractorBuilder(() -> m -> m.flatMap(r -> DataBufferUtils.join(r.getBody())).flatMap(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return Mono.just(GsonUtils.INS.fromJson(new String(bytes, StandardCharsets.UTF_8), new TypeToken<List<Map<String, Object>>>() {
                    }));
                }))
                .setSubFloxBuilder(() -> new DefaultSubFlox(
                        List.class,
                        List.class,
                        List.of(new NodeEntity(
                                "insertDataSource",
                                NodeType.DATA_SOURCE_LOADER,
                                new DataSourceLoader(),
                                Map.of(DefaultSubFlox.DATA_SOURCE_CODE, MetaDataSourceConfig.META_DATA_SOURCE_KEY,
                                        DefaultSubFlox.ACTION_CODE, "insert_data_source"),
                                List.of(List.class),
                                List.class,
                                "insertDataSource",
                                List.of(DefaultSubFlox.PARAM_NODE_CODE)
                        )),
                        dataSourceManager
                ))
                .setResponseLoaderBuilder(DefaultResponseLoader::new);
        return new ApiEndPoint("/data-source/insert", ApiMethod.POST, builder.builder());
    }
}
