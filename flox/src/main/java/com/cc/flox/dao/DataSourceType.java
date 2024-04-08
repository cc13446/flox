package com.cc.flox.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 数据源类型
 *
 * @author cc
 * @date 2024/4/6
 */
@AllArgsConstructor
@Getter
public enum DataSourceType {

    /**
     * postgresql
     */
    Postgresql("postgresql"),

    /**
     * mysql
     */
    Mysql("mysql");

    /**
     * jdbc url 中数据库的 code
     */
    private final String urlCode;

    public static DataSourceType fromUrlCode(String urlCode) {
        return Arrays.stream(DataSourceType.values()).filter(v -> v.getUrlCode().equals(urlCode)).findFirst().orElse(null);
    }

}
