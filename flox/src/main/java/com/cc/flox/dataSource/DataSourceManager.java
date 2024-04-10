package com.cc.flox.dataSource;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.Option;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据源管理者
 *
 * @author cc
 * @date 2024/4/4
 */
@Component
public class DataSourceManager {

    /**
     * 数据源 Map
     */
    private final Map<String, DataSource> dataSourceMap = new ConcurrentHashMap<>();

    /**
     * 数据源插入
     *
     * @param dataSourceConfig 数据源配置
     */
    public void insert(DataSourceConfiguration dataSourceConfig) {
        dataSourceMap.compute(dataSourceConfig.id(), (key, oldPool) -> {
            if (Objects.isNull(oldPool)) {
                return new DataSource(key, new R2dbcEntityTemplate(new ConnectionPool(getConnectionPoolConfiguration(dataSourceConfig))));
            }
            throw new RuntimeException("Insert dataSource fail : existed");
        });
    }


    /**
     * @param dataSourceConfig 数据源配置
     * @return 数据库连接池配置
     */
    private ConnectionPoolConfiguration getConnectionPoolConfiguration(DataSourceConfiguration dataSourceConfig) {
        ConnectionFactoryOptions options = ConnectionFactoryOptions.builder()
                .from(ConnectionFactoryOptions.parse(dataSourceConfig.url()))
                .option(Option.valueOf(DataSourceConfiguration.USER), dataSourceConfig.username())
                .option(Option.valueOf(DataSourceConfiguration.PASSWORD), dataSourceConfig.password())
                .build();

        ConnectionFactory factory = ConnectionFactories.get(options);
        return ConnectionPoolConfiguration.builder(factory)
                .initialSize(dataSourceConfig.initSize())
                .maxSize(dataSourceConfig.maxSize())
                .maxIdleTime(Duration.ofSeconds(dataSourceConfig.maxIdle()))
                .build();
    }
}
