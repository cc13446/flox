package com.cc.flox.meta.entity;

import com.cc.flox.dataSource.DataSource;
import com.cc.flox.dataSource.DataSourceConfiguration;
import com.cc.flox.dataSource.DataSourceType;
import com.cc.flox.dataSource.action.Action;
import com.cc.flox.dataSource.template.TemplateType;
import com.cc.flox.meta.Constant;
import com.cc.flox.utils.AssertUtils;
import com.cc.flox.utils.GsonUtils;
import com.google.gson.reflect.TypeToken;
import io.r2dbc.pool.ConnectionPool;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.util.CollectionUtils;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author cc
 * @date 2024/5/1
 */
public record DataSourcesEntity(
        List<Map<String, Object>> dataSources,
        List<Map<String, Object>> actions) {

    /**
     * @return 是否需要更新
     */
    public boolean update() {
        return !CollectionUtils.isEmpty(dataSources) || !CollectionUtils.isEmpty(actions);
    }

    /**
     * @return 更新时间
     */
    public OffsetDateTime getUpdateTime() {
        OffsetDateTime dataSourceUpdateTime = dataSources.stream().filter(m -> Objects.nonNull(m.get(Constant._UPDATE_TIME))).map(m -> (OffsetDateTime) m.get(Constant._UPDATE_TIME)).max(OffsetDateTime::compareTo).orElse(OffsetDateTime.MIN);
        OffsetDateTime actionSourceUpdateTime = actions.stream().filter(m -> Objects.nonNull(m.get(Constant._UPDATE_TIME))).map(m -> (OffsetDateTime) m.get(Constant._UPDATE_TIME)).max(OffsetDateTime::compareTo).orElse(OffsetDateTime.MIN);
        return dataSourceUpdateTime.isAfter(actionSourceUpdateTime) ? dataSourceUpdateTime : actionSourceUpdateTime;
    }

    /**
     * @return 失效的数据源
     */
    public Set<String> getInvalidDataSource() {
        return dataSources.stream().filter(m -> !Boolean.parseBoolean(m.get(Constant.STATUS).toString())).map(m -> m.get(Constant.CODE).toString()).collect(Collectors.toSet());
    }

    /**
     * @return 失效的动作 -> 数据源
     */
    public Map<String, String> getInvalidAction() {
        return actions.stream().filter(m -> !Boolean.parseBoolean(m.get(Constant.STATUS).toString()))
                .collect(Collectors.toMap(m -> m.get(Constant.CODE).toString(), m -> m.get(Constant._DATA_SOURCE_CODE).toString()));
    }

    /**
     * @return 有效的 data source
     */
    public List<DataSource> getDataSources() {
        Map<String, List<Action>> actionMap = actions.stream().filter(m -> Boolean.parseBoolean(m.get(Constant.STATUS).toString()))
                .collect(Collectors.groupingBy(m -> m.get(Constant._DATA_SOURCE_CODE).toString(), Collectors.mapping(m -> new Action(
                        m.get(Constant.CODE).toString(),
                        AssertUtils.assertNonNull(TemplateType.fromCode(m.get(Constant.TYPE).toString()), "Unknown template type " + m.get(Constant.TYPE)),
                        m.get(Constant.SQL).toString()
                ), Collectors.toList())));
        return dataSources.stream().filter(m -> Boolean.parseBoolean(m.get(Constant.STATUS).toString()))
                .map(m -> {
                    Map<String, Object> config = GsonUtils.INS.fromJson(m.get(Constant.CONFIG).toString(), new TypeToken<>() {
                    });
                    if (CollectionUtils.isEmpty(config)) {
                        config = HashMap.newHashMap(0);
                    }
                    return new DataSourceConfiguration(
                            m.get(Constant.CODE).toString(),
                            m.get(Constant.URL).toString(),
                            m.get(Constant.USERNAME).toString(),
                            m.get(Constant.PASSWORD).toString(),
                            AssertUtils.assertNonNull(DataSourceType.fromCode(m.get(Constant.TYPE).toString()), "Unknown data source type" + m.get(Constant.TYPE)),
                            Integer.parseInt(config.getOrDefault(Constant.INIT_SIZE, DataSourceConfiguration.DEFAULT_INIT_SIZE).toString()),
                            Integer.parseInt(config.getOrDefault(Constant.MAX_SIZE, DataSourceConfiguration.DEFAULT_MAX_SIZE).toString()),
                            Integer.parseInt(config.getOrDefault(Constant.MAX_IDLE, DataSourceConfiguration.DEFAULT_MAX_IDLE).toString()),
                            actionMap.getOrDefault(m.get(Constant.CODE).toString(), List.of()).stream().collect(Collectors.toMap(Action::getCode, s -> s)));
                })
                .map(c -> new DataSource(
                        c.code(),
                        new R2dbcEntityTemplate(new ConnectionPool(c.getConnectionPoolConfiguration())),
                        c.action(),
                        c.type())).toList();
    }


}
