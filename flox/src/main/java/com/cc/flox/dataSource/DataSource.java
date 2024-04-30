package com.cc.flox.dataSource;

import com.cc.flox.dataSource.action.Action;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;

import java.util.Map;

/**
 * 数据源
 *
 * @author cc
 * @date 2024/4/6
 */
@AllArgsConstructor
@Getter
public class DataSource {

    /**
     * 数据源code
     */
    private final String code;

    /**
     * 数据库连接池
     */
    private final R2dbcEntityTemplate template;

    /**
     * 数据库动作
     */
    private final Map<String, Action> actions;

    /**
     * 数据源类型
     */
    private final DataSourceType dataSourceType;
}
