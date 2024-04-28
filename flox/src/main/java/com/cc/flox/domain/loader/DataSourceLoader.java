package com.cc.flox.domain.loader;

import com.cc.flox.dataSource.DataSourceManager;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * @author cc
 * @date 2024/4/12
 */
public class DataSourceLoader implements Loader<Map<String, Object>, DataSourceManager, List<Map<String, Object>>> {

    /**
     * dataSourceCode
     */
    public static final String DATA_SOURCE_CODE = "dataSourceCode";

    /**
     * actionCode
     */
    public static final String ACTION_CODE = "actionCode";

    @Override
    public Mono<List<Map<String, Object>>> loader(Mono<Map<String, Object>> param, Mono<DataSourceManager> dataSourceManager, Mono<Map<String, Object>> attribute) {
        return Mono.zip(param, dataSourceManager, attribute).flatMap(t -> t.getT2().exec(t.getT3().get(DATA_SOURCE_CODE).toString(), t.getT3().get(ACTION_CODE).toString(), t.getT1()));
    }
}
