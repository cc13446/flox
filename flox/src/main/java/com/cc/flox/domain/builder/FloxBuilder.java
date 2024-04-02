package com.cc.flox.domain.builder;

import com.cc.flox.domain.Flox;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 流程构建者
 *
 * @author cc
 * @date 2024/4/2
 */
@Data
@Accessors(chain = true)
public class FloxBuilder {

    /**
     * HTTP请求提取器构建者
     */
    private Supplier<Function<ServerHttpRequest, Object>> requestExtractorBuilder;

    /**
     * HTTP响应加载器构建者
     */
    private Supplier<BiConsumer<Object, ServerHttpResponse>> responseLoaderBuilder;

    /**
     * 构建方法
     *
     * @return flox
     */
    public Flox builder() {
        return new Flox(requestExtractorBuilder.get(), responseLoaderBuilder.get());
    }
}
