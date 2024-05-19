package com.cc.flox.domain.loader;

import com.cc.flox.dataSource.DataSourceManager;
import com.cc.flox.domain.node.NodeExecContext;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * @author cc
 * @date 2024/4/12
 */
public class DataSourceLoader implements Loader<Object, DataSourceManager, List<Map<String, Object>>> {

    /**
     * param
     */
    public static final String PARAM = "param";

    @Override
    @SuppressWarnings("unchecked")
    public Mono<List<Map<String, Object>>> loader(Mono<Object> param, Mono<DataSourceManager> dataSourceManager, Mono<NodeExecContext> context) {
        Mono<Map<String, Object>> p = param.map(o -> {
            if (Map.class.isAssignableFrom(o.getClass())) {
                return (Map<String, Object>) o;
            } else {
                return Map.of(PARAM, o);
            }
        });
        return Mono.zip(p, dataSourceManager, context).flatMap(t -> t.getT2().exec(t.getT3(), t.getT1()));
    }
}
