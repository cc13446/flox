package com.cc.flox.domain.flox;

import com.cc.flox.meta.entity.FloxEntity;
import com.cc.flox.meta.entity.NodeEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
public class FloxBuilder {

    public FloxBuilder(FloxEntity entity) {
        this.requestExtractorBuilder = entity::requestExtractor;
        this.subFloxBuilder = entity::subFlox;
        this.responseLoaderBuilder = entity::responseLoader;
    }

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
    public Flox build() {
        return new Flox(requestExtractorBuilder.get(), subFloxBuilder.get(), responseLoaderBuilder.get());
    }
}
