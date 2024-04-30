package com.cc.flox.dataSource;

import com.cc.flox.dataSource.action.Action;
import com.cc.flox.meta.Constant;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.Option;

import java.time.Duration;
import java.util.Map;

/**
 * 数据源配置
 *
 * @author cc
 * @date 2024/4/6
 */
public record DataSourceConfiguration(
        String code,

        // 数据库配置
        String url, String username, String password,

        // 数据库类型
        DataSourceType type,

        // 连接池配置
        int initSize, int maxSize, int maxIdle,

        // 数据库动作
        Map<String, Action> action) {

    public static final int DEFAULT_INIT_SIZE = 10;
    public static final int DEFAULT_MAX_SIZE = 20;
    public static final int DEFAULT_MAX_IDLE = 60;

    /**
     * @return 数据库连接池配置
     */
    public ConnectionPoolConfiguration getConnectionPoolConfiguration() {
        ConnectionFactoryOptions options = ConnectionFactoryOptions.builder()
                .from(ConnectionFactoryOptions.parse(url))
                .option(Option.valueOf(Constant.USER), username)
                .option(Option.valueOf(Constant.PASSWORD), password)
                .build();

        ConnectionFactory factory = ConnectionFactories.get(options);
        return ConnectionPoolConfiguration.builder(factory)
                .initialSize(initSize)
                .maxSize(maxSize)
                .maxIdleTime(Duration.ofSeconds(maxIdle))
                .build();
    }

}
