package com.cc.flox.domain.loader;

import com.cc.flox.dataSource.DataSourceManager;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * @author cc
 * @date 2024/4/12
 */
public class DataSourceLoader implements Loader<Object, DataSourceManager, List<Map<String, Object>>> {

    /**
     * dataSourceCode
     */
    public static final String DATA_SOURCE_CODE = "dataSourceCode";

    /**
     * actionCode
     */
    public static final String ACTION_CODE = "actionCode";

    /**
     * param
     */
    public static final String PARAM = "param";

    @Override
    @SuppressWarnings("unchecked")
    public Mono<List<Map<String, Object>>> loader(Mono<Object> param, Mono<DataSourceManager> dataSourceManager, Mono<Map<String, Object>> attribute) {
        Mono<Map<String, Object>> p = param.map(o -> {
            if (Map.class.isAssignableFrom(o.getClass())) {
                return (Map<String, Object>) o;
            } else {
                return Map.of(PARAM, o);
            }
        });
        return Mono.zip(p, dataSourceManager, attribute).flatMap(t -> t.getT2().exec(t.getT3().get(DATA_SOURCE_CODE).toString(), t.getT3().get(ACTION_CODE).toString(), t.getT1()));
    }
}
