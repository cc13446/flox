package com.cc.flox.domain.loader.dataSourceLoader;

import java.util.Map;

/**
 * 数据源加载器参数
 *
 * @author cc
 * @date 2024/4/12
 */
public record DataSourceLoaderParam(
        String dataSourceCode,
        String actionCode,
        Map<String, Object> param) {
}
