package com.cc.flox.dataSource.action;

import lombok.AllArgsConstructor;
import lombok.Getter;

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
    StringTemplate("stringTemplate");

    private final String code;

}
