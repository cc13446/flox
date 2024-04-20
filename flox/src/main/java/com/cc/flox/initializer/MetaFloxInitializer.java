package com.cc.flox.initializer;

import com.cc.flox.api.ApiManager;
import com.cc.flox.api.endpoint.ApiEndPoint;
import com.cc.flox.api.endpoint.ApiMethod;
import com.cc.flox.dataSource.DataSourceManager;
import com.cc.flox.domain.flox.FloxBuilder;
import com.cc.flox.domain.node.NodeType;
import com.cc.flox.domain.subFlox.impl.DefaultSubFlox;
import com.cc.flox.domain.transformer.Transformer;
import com.cc.flox.meta.entity.NodeEntity;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;


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
    }

    /**
     * @return echo end point
     */
    private ApiEndPoint getEchoEndPoint() {
        FloxBuilder builder = new FloxBuilder()
                .setRequestExtractorBuilder(() -> m -> m.map(r -> r.getQueryParams().entrySet().stream()
                        .map(e -> e.getKey() + "=[" + e.getValue().stream().reduce(new StringBuilder(), StringBuilder::append, StringBuilder::append).toString() + "]\n")
                        .reduce(new StringBuilder(), StringBuilder::append, StringBuilder::append).toString()))
                .setResponseLoaderBuilder(() -> (o, r) ->
                        Mono.zip(o, r).publishOn(Schedulers.boundedElastic()).handle((t, sink) -> {
                            t.getT2().setStatusCode(HttpStatus.OK);
                            String res = (String) t.getT1();
                            DataBuffer dataBuffer = t.getT2().bufferFactory().allocateBuffer(res.length());
                            try (OutputStream outputStream = dataBuffer.asOutputStream()) {
                                outputStream.write(res.getBytes(StandardCharsets.UTF_8));
                            } catch (IOException ioException) {
                                sink.error(new RuntimeException(ioException));
                                return;
                            }
                            t.getT2().writeWith(Mono.just(dataBuffer)).block();
                            sink.complete();
                        }))
                .setSubFloxBuilder(() -> new DefaultSubFlox(
                        String.class,
                        String.class,
                        List.of(new NodeEntity(
                                "identify",
                                NodeType.TRANSFORMER,
                                (Transformer<String, String>) source -> source,
                                HashMap.newHashMap(1),
                                List.of(String.class),
                                String.class,
                                "echo",
                                List.of(DefaultSubFlox.PARAM_NODE_CODE)
                        )),
                        dataSourceManager
                ));
        return new ApiEndPoint("/echo", ApiMethod.GET, builder.builder());
    }

    /**
     * @return insert data source end point
     */
    private ApiEndPoint getInsertDataSourceEndPoint() {
        return null;
    }
}
