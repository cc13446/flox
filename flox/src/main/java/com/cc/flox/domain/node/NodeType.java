package com.cc.flox.domain.node;

import com.cc.flox.dataSource.DataSourceManager;
import com.cc.flox.domain.extractor.Extractor;
import com.cc.flox.domain.extractor.RequestExtractor;
import com.cc.flox.domain.loader.Loader;
import com.cc.flox.domain.loader.ResponseLoader;
import com.cc.flox.domain.loader.dataSourceLoader.DataSourceLoader;
import com.cc.flox.domain.loader.dataSourceLoader.DataSourceLoaderParam;
import com.cc.flox.domain.subFlox.SubFlox;
import com.cc.flox.domain.transformer.BiTransformer;
import com.cc.flox.domain.transformer.Transformer;
import com.cc.flox.domain.transformer.TriTransformer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import reactor.core.publisher.Mono;

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

    SUB_FLOX("subFlox", 1, SubFlox.class, (n, p) -> ((SubFlox) n).handle(p.getFirst())),

    EXTRACTOR("extractor", 1, Extractor.class, (n, p) -> ((Extractor) n).extract(p.getFirst())),

    REQUEST_EXTRACTOR("requestExtractor", 0, RequestExtractor.class, (n, p) -> {
        throw new RuntimeException("Do not support request extractor in sub flox");
    }),

    LOADER("loader", 2, Loader.class, (n, p) -> ((Loader) n).loader(p.getFirst(), p.get(1))),

    RESPONSE_LOADER("responseLoader", 1, ResponseLoader.class, (n, p) -> {
        throw new RuntimeException("Do not support response loader in sub flox");
    }),

    DATA_SOURCE_LOADER("dataSourceLoader", 1, DataSourceLoader.class, (n, p) -> (Mono<Object>) (Mono) ((DataSourceLoader) n).loader((Mono<DataSourceLoaderParam>) (Mono) p.getFirst(), (Mono<DataSourceManager>) (Mono) p.get(1))),

    TRANSFORMER("transformer", 1, Transformer.class, (n, p) -> ((Transformer) n).transform(p.getFirst())),

    BI_TRANSFORMER("biTransformer", 2, BiTransformer.class, (n, p) -> ((BiTransformer) n).transform(p.getFirst(), p.get(1))),

    TRI_TRANSFORMER("triTransformer", 3, TriTransformer.class, (n, p) -> ((TriTransformer) n).transform(p.getFirst(), p.get(1), p.get(2)));

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
    private final Class<?> clazz;

    /**
     * 执行逻辑
     */
    private final NodeExecFunction execFunction;

}
