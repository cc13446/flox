package com.cc.flox.dataSource;

import com.cc.flox.dataSource.action.Action;

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

    /**
     * 用户名
     */
    public static final String USER = "user";

    /**
     * 密码
     */
    public static final String PASSWORD = "password";

}
