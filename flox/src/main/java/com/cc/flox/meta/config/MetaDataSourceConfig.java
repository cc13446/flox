package com.cc.flox.meta.config;

import com.cc.flox.dao.DataSourceConfiguration;
import com.cc.flox.dao.DataSourceType;
import com.cc.flox.utils.AssertUtils;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.Option;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 元数据库配置
 *
 * @author cc
 * @date 2024/4/6
 */
@Getter
@Configuration
public class MetaDataSourceConfig {
    /**
     * 元数据库 key
     */
    public static final String DATA_SOURCE_META_KEY = "META_DATA_SOURCE";

    /**
     * 数据源 url
     */
    @Value("${meta.dataSource.url}")
    private String url;

    /**
     * 用户名
     */
    @Value("${meta.dataSource.username}")
    private String username;

    /**
     * 密码
     */
    @Value("${meta.dataSource.password}")
    private String password;

    /**
     * 连接池初始连接数
     */
    @Value("${meta.dataSource.pool.initSize:5}")
    private int initSize;

    /**
     * 连接池最大连接数
     */
    @Value("${meta.dataSource.pool.maxSize:20}")
    private int maxSize;

    /**
     * 连接池连接最大空闲时间
     */
    @Value("${meta.dataSource.pool.maxIdle:60}")
    private int maxIdle;

    /**
     * @return 元数据库配置对象
     */
    public DataSourceConfiguration getDataSourceConfiguration() {
        ConnectionFactoryOptions options = ConnectionFactoryOptions.parse(url);
        String dataSourceType = (String) checkAndGetValue(options, "driver");
        return new DataSourceConfiguration(
                DATA_SOURCE_META_KEY,
                (String) checkAndGetValue(options, "host"),
                (Integer) checkAndGetValue(options, "port"),
                (String) checkAndGetValue(options, "database"),
                StringUtils.isNoneBlank(username) ? username : (String) checkAndGetValue(options, "username"),
                StringUtils.isNoneBlank(password) ? password : (String) checkAndGetValue(options, "password"),
                AssertUtils.assertNonNull(DataSourceType.fromUrlCode(dataSourceType), "Unknown dataSource type : " + dataSourceType),
                initSize,
                maxSize,
                maxIdle
        );
    }

    /**
     * 检查并获取值
     *
     * @param options ConnectionFactoryOptions
     * @param name    option name
     * @return value
     */
    private Object checkAndGetValue(ConnectionFactoryOptions options, String name) {
        String errorMessage = "Meta dataSource [" + name + "] cannot be null";
        return AssertUtils.assertNonNull(options.getValue(Option.valueOf(name)), errorMessage);
    }

}
