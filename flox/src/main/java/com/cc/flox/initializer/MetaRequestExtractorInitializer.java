package com.cc.flox.initializer;

import com.cc.flox.node.NodeManager;
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
import java.util.List;
import java.util.Map;


/**
 * 元请求抽取器初始化器
 *
 * @author cc
 * @date 2024/4/2
 */
@Order(4)
@Component
public class MetaRequestExtractorInitializer implements CommandLineRunner {

    public static final String META_REQUEST_EXTRACTOR_CODE_QUERY_PARAMS = "meta_request_extractor_query_params";
    public static final String META_REQUEST_EXTRACTOR_CODE_BODY_PARAMS = "meta_request_extractor_body_params";

    @Resource
    private NodeManager nodeManager;

    @Override
    public void run(String... args) {
        nodeManager.putRequestExtract(META_REQUEST_EXTRACTOR_CODE_QUERY_PARAMS, m -> m.map(ServerHttpRequest::getQueryParams));
        nodeManager.putRequestExtract(META_REQUEST_EXTRACTOR_CODE_BODY_PARAMS, m -> m.flatMap(r -> DataBufferUtils.join(r.getBody())).flatMap(dataBuffer -> {
            byte[] bytes = new byte[dataBuffer.readableByteCount()];
            dataBuffer.read(bytes);
            DataBufferUtils.release(dataBuffer);
            return Mono.just(GsonUtils.INS.fromJson(new String(bytes, StandardCharsets.UTF_8), new TypeToken<List<Map<String, Object>>>() {
            }));
        }));
    }

}
