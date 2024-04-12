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
    public Mono<List<Map<String, Object>>> loader(DataSourceLoaderParam param, DataSourceManager dataSourceManager) {
        return dataSourceManager.exec(param.dataSourceCode(), param.actionCode(), param.param());
    }
}
