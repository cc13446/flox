package com.cc.flox.domain.flox;

import com.cc.flox.meta.entity.NodeEntity;
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
    private Supplier<NodeEntity> requestExtractorBuilder;

    /**
     * sub flox 构建者
     */
    private Supplier<NodeEntity> subFloxBuilder;

    /**
     * HTTP响应加载器构建者
     */
    private Supplier<NodeEntity> responseLoaderBuilder;

    /**
     * 构建方法
     *
     * @return flox
     */
    public Flox builder() {
        return new Flox(requestExtractorBuilder.get(), subFloxBuilder.get(), responseLoaderBuilder.get());
    }
}
