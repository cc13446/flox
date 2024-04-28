package com.cc.flox.initializer;

import com.cc.flox.api.response.ApiResponseWrapper;
import com.cc.flox.domain.loader.ResponseLoader;
import com.cc.flox.domain.node.NodeType;
import com.cc.flox.meta.entity.NodeEntity;
import com.cc.flox.node.NodeManager;
import com.cc.flox.utils.GsonUtils;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.charset.StandardCharsets;
import java.util.List;


/**
 * 元回复加载器初始化器
 *
 * @author cc
 * @date 2024/4/2
 */
@Order(4)
@Component
public class MetaResponseLoaderInitializer implements CommandLineRunner {

    public static final String META_RESPONSE_LOADER_CODE_WRITE_JSON = "meta_response_loader_write_json";

    @Resource
    private NodeManager nodeManager;

    @Override
    public void run(String... args) {
        nodeManager.putResponseLoader(new NodeEntity(
                META_RESPONSE_LOADER_CODE_WRITE_JSON,
                NodeType.RESPONSE_LOADER,
                (ResponseLoader<Object>) (source, destination, attribute) -> Mono.zip(source, destination).publishOn(Schedulers.boundedElastic()).handle((t, sink) -> {
                    ServerHttpResponse response = t.getT2();
                    response.setStatusCode(HttpStatus.OK);
                    String res = GsonUtils.INS.toJson(ApiResponseWrapper.success(t.getT1()));
                    response.writeWith(Mono.just(response.bufferFactory().wrap(res.getBytes(StandardCharsets.UTF_8)))).block();
                    sink.complete();
                }),
                List.of(Object.class, ServerHttpResponse.class),
                Void.class));
    }
}
