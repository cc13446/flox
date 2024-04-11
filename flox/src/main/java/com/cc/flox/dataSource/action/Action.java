package com.cc.flox.dataSource.action;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据源动作
 *
 * @author cc
 * @date 2024/4/10
 */
@AllArgsConstructor
@Getter
public class Action {

    /**
     * code
     */
    private String code;

    /**
     * 类型
     */
    private ActionType type;

    /**
     * sql
     */
    private String sql;
}
