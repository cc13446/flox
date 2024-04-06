package com.cc.flox.dao;

import io.asyncer.r2dbc.mysql.MySqlConnectionConfiguration;
import io.asyncer.r2dbc.mysql.MySqlConnectionFactory;
import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
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
                return new DataSource(key, new ConnectionPool(getConnectionPoolConfiguration(dataSourceConfig)));
            }
            throw new RuntimeException("Insert dataSource fail : existed");
        });
    }


    /**
     * @param dataSourceConfig 数据源配置
     * @return 数据库连接池配置
     */
    private ConnectionPoolConfiguration getConnectionPoolConfiguration(DataSourceConfiguration dataSourceConfig) {
        return ConnectionPoolConfiguration.builder(
                        switch (dataSourceConfig.type()) {
                            case Mysql -> MySqlConnectionFactory.from(MySqlConnectionConfiguration.builder()
                                    .host(dataSourceConfig.host())
                                    .port(dataSourceConfig.port())
                                    .database(dataSourceConfig.database())
                                    .username(dataSourceConfig.username())
                                    .password(dataSourceConfig.password())
                                    .build());
                            case Postgresql -> new PostgresqlConnectionFactory(PostgresqlConnectionConfiguration.builder()
                                    .host(dataSourceConfig.host())
                                    .port(dataSourceConfig.port())
                                    .database(dataSourceConfig.database())
                                    .username(dataSourceConfig.username())
                                    .password(dataSourceConfig.password())
                                    .build());
                            case null -> throw new RuntimeException("Insert dataSource fail : the type is null");
                        })
                .initialSize(dataSourceConfig.initSize())
                .maxSize(dataSourceConfig.maxSize())
                .maxIdleTime(Duration.ofSeconds(dataSourceConfig.maxIdle()))
                .build();
    }
}
