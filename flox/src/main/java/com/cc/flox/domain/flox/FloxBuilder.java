package com.cc.flox.domain.flox;

import com.cc.flox.domain.extractor.RequestExtractor;
import com.cc.flox.domain.loader.ResponseLoader;
import com.cc.flox.domain.subFlox.SubFlox;
import lombok.Data;
import lombok.experimental.Accessors;

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
    private Supplier<RequestExtractor<Object>> requestExtractorBuilder;

    /**
     * sub flox 构建者
     */
    private Supplier<SubFlox> subFloxBuilder;

    /**
     * HTTP响应加载器构建者
     */
    private Supplier<ResponseLoader<Object>> responseLoaderBuilder;

    /**
     * 构建方法
     *
     * @return flox
     */
    public Flox builder() {
        return new Flox(requestExtractorBuilder.get(), subFloxBuilder.get(), responseLoaderBuilder.get());
    }
}
