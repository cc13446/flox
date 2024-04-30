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
    Postgresql("postgresql", PlaceHolderType.DOLLAR),

    /**
     * mysql
     */
    Mysql("mysql", PlaceHolderType.QUESTION_MARK);

    /**
     * jdbc url 中数据库的 code
     */
    private final String code;

    /**
     * 占位符类型
     */
    private final PlaceHolderType placeHolderType;

    public static DataSourceType fromCode(String code) {
        return Arrays.stream(DataSourceType.values()).filter(v -> v.getCode().equals(code)).findFirst().orElse(null);
    }

}
