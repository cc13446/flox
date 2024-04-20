package com.cc.flox.dataSource;

import com.cc.flox.dataSource.action.Action;
import com.cc.flox.dataSource.template.TemplateRenderContext;
import com.cc.flox.dataSource.template.TemplateRenderExecutor;
import com.cc.flox.executor.ExecutorInvoker;
import com.cc.flox.utils.AssertUtils;
import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.Option;
import jakarta.annotation.Resource;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
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

    @Resource
    private ExecutorInvoker invoker;

    /**
     * 数据源 Map
     */
    private final Map<String, DataSource> dataSources = new ConcurrentHashMap<>();

    /**
     * @param code code
     * @return code 对应的数据源
     */
    public DataSource get(String code) {
        return dataSources.get(code);
    }

    /**
     * 数据源插入
     *
     * @param dataSourceConfig 数据源配置
     */
    public void insert(DataSourceConfiguration dataSourceConfig) {
        dataSources.compute(dataSourceConfig.code(), (key, oldPool) -> {
            if (Objects.isNull(oldPool)) {
                return new DataSource(
                        key,
                        new R2dbcEntityTemplate(new ConnectionPool(getConnectionPoolConfiguration(dataSourceConfig))),
                        dataSourceConfig.action());
            }
            throw new RuntimeException("Insert dataSource fail : existed");
        });
    }

    /**
     * 执行动作
     *
     * @param dataSourceCode dataSourceCode
     * @param actionCode     actionCode
     * @param param          参数
     * @return 结果
     */
    public Mono<List<Map<String, Object>>> exec(String dataSourceCode, String actionCode, Map<String, Object> param) {
        DataSource dataSource = this.dataSources.get(dataSourceCode);
        AssertUtils.assertNonNull(dataSourceCode, "Exec data source action error, unknown data source : " + dataSourceCode);

        Action action = dataSource.getActions().get(actionCode);
        AssertUtils.assertNonNull(action, "Exec data source action error, unknown data source action: " + actionCode);

        TemplateRenderContext context = new TemplateRenderContext(action, param);
        context = invoker.invoke(TemplateRenderExecutor.class, context);

        DatabaseClient.GenericExecuteSpec spec = dataSource.getTemplate().getDatabaseClient().sql(context.getRenderedSQL());
        if (!CollectionUtils.isEmpty(context.getRenderedParam())) {
            spec = spec.bindValues(context.getRenderedParam());
        }
        return spec.fetch().all().collectList();
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
