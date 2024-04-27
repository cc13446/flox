package com.cc.flox.initializer;

import com.cc.flox.api.response.ApiResponseWrapper;
import com.cc.flox.node.NodeManager;
import com.cc.flox.utils.GsonUtils;
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
        nodeManager.putResponseLoader(META_RESPONSE_LOADER_CODE_WRITE_JSON, (source, destination) ->
                Mono.zip(source, destination).publishOn(Schedulers.boundedElastic()).handle((t, sink) -> {
                    t.getT2().setStatusCode(HttpStatus.OK);
                    String res = GsonUtils.INS.toJson(ApiResponseWrapper.success(t.getT1()));
                    DataBuffer dataBuffer = t.getT2().bufferFactory().allocateBuffer(res.length());
                    try (OutputStream outputStream = dataBuffer.asOutputStream()) {
                        outputStream.write(res.getBytes(StandardCharsets.UTF_8));
                    } catch (IOException ioException) {
                        sink.error(new RuntimeException(ioException));
                        return;
                    }
                    t.getT2().writeWith(Mono.just(dataBuffer)).block();
                    sink.complete();
                }));
    }
}
