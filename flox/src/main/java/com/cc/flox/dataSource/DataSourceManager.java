package com.cc.flox.dataSource;

import com.cc.flox.dataSource.action.Action;
import com.cc.flox.dataSource.template.TemplateRenderContext;
import com.cc.flox.dataSource.template.TemplateRenderExecutor;
import com.cc.flox.executor.ExecutorInvoker;
import com.cc.flox.node.NodeManager;
import com.cc.flox.utils.AssertUtils;
import com.cc.flox.utils.HolderUtils;
import com.cc.flox.utils.StreamUtils;
import io.r2dbc.pool.ConnectionPool;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 数据源管理者
 *
 * @author cc
 * @date 2024/4/4
 */
@Slf4j
@Component
public class DataSourceManager {

    /**
     * 是否启动
     */
    private final AtomicBoolean hasStart = new AtomicBoolean(false);

    /**
     * 数据源 Map
     */
    private final Map<String, DataSource> dataSources = new ConcurrentHashMap<>();

    @Resource
    private ExecutorInvoker invoker;

    @Resource
    private NodeManager nodeManager;

    /**
     * 开始同步数据源
     */
    public void startSynchronize() {
        if (hasStart.compareAndSet(false, true)) {
            Flux.interval(Duration.ofSeconds(10)).subscribe(l -> {
                log.info("Start synchronize data source, count {}", l);
            });
        }
    }

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
                        new R2dbcEntityTemplate(new ConnectionPool(dataSourceConfig.getConnectionPoolConfiguration())),
                        dataSourceConfig.action(),
                        dataSourceConfig.type());
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
    public Mono<List<Map<String, Object>>> exec(String dataSourceCode, String
            actionCode, Map<String, Object> param) {
        DataSource dataSource = this.dataSources.get(dataSourceCode);
        AssertUtils.assertNonNull(dataSourceCode, "Exec data source action error, unknown data source : " + dataSourceCode);

        Action action = dataSource.getActions().get(actionCode);
        AssertUtils.assertNonNull(action, "Exec data source action error, unknown data source action: " + actionCode);

        TemplateRenderContext context = new TemplateRenderContext(action, param, dataSource.getDataSourceType());
        context = invoker.invoke(TemplateRenderExecutor.class, context);

        HolderUtils<DatabaseClient.GenericExecuteSpec> spec = new HolderUtils<>(dataSource.getTemplate().getDatabaseClient().sql(context.getRenderedSQL()));
        if (context.isCustomBind()) {
            spec.setHolder(spec.getHolder().bindValues(context.getCustomBindParam()));
        } else if (!CollectionUtils.isEmpty(context.getRenderedParam())) {
            context.getRenderedParam().forEach(StreamUtils.withCounter((i, o) -> {
                if (PlaceHolderType.QUESTION_MARK.equals(dataSource.getDataSourceType().getPlaceHolderType())) {
                    spec.setHolder(spec.getHolder().bind(i, o));
                } else if (PlaceHolderType.DOLLAR.equals(dataSource.getDataSourceType().getPlaceHolderType())) {
                    spec.setHolder(spec.getHolder().bind("$" + (i + 1), o));
                } else {
                    throw new RuntimeException("Unknown data source place holder type " + dataSource.getDataSourceType().getPlaceHolderType());
                }
            }));
        }
        return spec.getHolder().fetch().all().collectList();
    }
}
