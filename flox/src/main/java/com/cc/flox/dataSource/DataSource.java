package com.cc.flox.dataSource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;

/** 数据源
 * @author cc
 * @date 2024/4/6
 */
@AllArgsConstructor
@Getter
public class DataSource {

    /**
     * 数据源id
     */
    private final String Id;

    /**
     * 数据库连接池
     */
    private final R2dbcEntityTemplate template;
}
