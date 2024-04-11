package com.cc.flox.dataSource.action;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 数据源动作类型
 *
 * @author cc
 * @date 2024/4/10
 */
@AllArgsConstructor
@Getter
public enum ActionType {

    /**
     * 原生sql
     */
    SQL("sql"),

    /**
     * string template 模板
     */
    StringTemplate("stl");

    private final String code;

    /**
     * @param code code
     * @return type
     */
    public static ActionType fromCode(String code) {
        return Arrays.stream(ActionType.values()).filter(t -> t.getCode().equals(code)).findFirst().orElse(null);
    }
}
