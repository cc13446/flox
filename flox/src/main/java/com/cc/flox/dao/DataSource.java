package com.cc.flox.dao;

import io.r2dbc.pool.ConnectionPool;
import lombok.AllArgsConstructor;
import lombok.Getter;

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
    private final ConnectionPool connectionPool;
}
