package com.cc.flox.dataSource.action;

import com.cc.flox.dataSource.template.TemplateType;
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
     * 模板类型
     */
    private TemplateType templateType;

    /**
     * sql
     */
    private String sql;
}
