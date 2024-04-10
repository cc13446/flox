package com.cc.flox.dataSource;

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
    private final String code;

    public static DataSourceType fromCode(String code) {
        return Arrays.stream(DataSourceType.values()).filter(v -> v.getCode().equals(code)).findFirst().orElse(null);
    }

}
