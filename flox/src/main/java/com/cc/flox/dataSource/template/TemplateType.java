package com.cc.flox.dataSource.template;

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
public enum TemplateType {

    /**
     * 原生sql
     */
    SQL("sql"),

    /**
     * string template 模板
     */
    StringTemplate("stg");

    private final String code;

    /**
     * @param code code
     * @return type
     */
    public static TemplateType fromCode(String code) {
        return Arrays.stream(TemplateType.values()).filter(t -> t.getCode().equals(code)).findFirst().orElse(null);
    }
}
