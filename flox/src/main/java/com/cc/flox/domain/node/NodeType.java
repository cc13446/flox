package com.cc.flox.domain.node;

import com.cc.flox.dataSource.DataSourceManager;
import com.cc.flox.domain.extractor.Extractor;
import com.cc.flox.domain.extractor.RequestExtractor;
import com.cc.flox.domain.loader.Loader;
import com.cc.flox.domain.loader.ResponseLoader;
import com.cc.flox.domain.loader.DataSourceLoader;
import com.cc.flox.domain.subFlox.SubFlox;
import com.cc.flox.domain.transformer.BiTransformer;
import com.cc.flox.domain.transformer.Transformer;
import com.cc.flox.domain.transformer.TriTransformer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import reactor.core.publisher.Mono;

import java.util.Arrays;

/**
 * 节点类型
 *
 * @author cc
 * @date 2024/4/14
 */
@AllArgsConstructor
@Getter
@SuppressWarnings({"unchecked", "rawtypes"})
public enum NodeType {

    SUB_FLOX("subFlox", 1, SubFlox.class, (n, p, a) -> ((SubFlox) n).handle(p.getFirst(), a)),

    EXTRACTOR("extractor", 1, Extractor.class, (n, p, a) -> ((Extractor) n).extract(p.getFirst(), a)),

    REQUEST_EXTRACTOR("requestExtractor", 1, RequestExtractor.class, (n, p, a) -> ((RequestExtractor) n).extract(p.getFirst(), a)),

    LOADER("loader", 2, Loader.class, (n, p, a) -> ((Loader) n).loader(p.getFirst(), p.get(1), a)),

    RESPONSE_LOADER("responseLoader", 2, ResponseLoader.class, (n, p, a) -> ((ResponseLoader) n).loader(p.getFirst(), p.get(1), a)),

    DATA_SOURCE_LOADER("dataSourceLoader", 2, DataSourceLoader.class, (n, p, a) -> (Mono<Object>) (Mono) ((DataSourceLoader) n).loader(p.getFirst(), (Mono<DataSourceManager>) (Mono) p.get(1), a)),

    TRANSFORMER("transformer", 1, Transformer.class, (n, p, a) -> ((Transformer) n).transform(p.getFirst(), a)),

    BI_TRANSFORMER("biTransformer", 2, BiTransformer.class, (n, p, a) -> ((BiTransformer) n).transform(p.getFirst(), p.get(1), a)),

    TRI_TRANSFORMER("triTransformer", 3, TriTransformer.class, (n, p, a) -> ((TriTransformer) n).transform(p.getFirst(), p.get(1), p.get(2), a));

    /**
     * code
     */
    private final String code;

    /**
     * 参数数量
     */
    private final int paramSize;

    /**
     * 类型
     */
    private final Class<? extends Node> clazz;

    /**
     * 执行逻辑
     */
    private final NodeExecFunction execFunction;

    /**
     * @param code code
     * @return type
     */
    public static NodeType fromCode(String code) {
        return Arrays.stream(NodeType.values()).filter(n -> n.getCode().equals(code)).findFirst().orElse(null);
    }

}
