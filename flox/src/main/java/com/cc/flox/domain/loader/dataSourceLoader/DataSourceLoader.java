package com.cc.flox.domain.loader.dataSourceLoader;

import com.cc.flox.dataSource.DataSourceManager;
import com.cc.flox.domain.loader.Loader;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * @author cc
 * @date 2024/4/12
 */
public class DataSourceLoader implements Loader<DataSourceLoaderParam, DataSourceManager, List<Map<String, Object>>> {

    @Override
    public Mono<List<Map<String, Object>>> loader(Mono<DataSourceLoaderParam> param, Mono<DataSourceManager> dataSourceManager) {
        return Mono.zip(param, dataSourceManager).flatMap(t -> t.getT2().exec(t.getT1().dataSourceCode(), t.getT1().actionCode(), t.getT1().param()));
    }
}
