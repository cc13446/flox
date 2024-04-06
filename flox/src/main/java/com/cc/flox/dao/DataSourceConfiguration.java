package com.cc.flox.dao;


/**
 * 数据源配置
 *
 * @author cc
 * @date 2024/4/6
 */
public record DataSourceConfiguration(
        String id,

        // 数据库配置
        String host,
        int port,
        String database,
        String username,
        String password,
        DataSourceType type,

        // 连接池配置
        int initSize,
        int maxSize,
        int maxIdle
) {
}
