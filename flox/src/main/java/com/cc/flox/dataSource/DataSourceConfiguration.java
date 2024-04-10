package com.cc.flox.dataSource;

/**
 * 数据源配置
 *
 * @author cc
 * @date 2024/4/6
 */
public record DataSourceConfiguration(
        String id,

        // 数据库配置
        String url, String username, String password,

        // 数据库类型
        DataSourceType type,

        // 连接池配置
        int initSize, int maxSize, int maxIdle) {

    /**
     * 用户名
     */
    public static final String USER = "user";

    /**
     * 密码
     */
    public static final String PASSWORD = "password";

}
