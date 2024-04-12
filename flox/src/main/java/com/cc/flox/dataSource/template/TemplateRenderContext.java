package com.cc.flox.dataSource.template;

import com.cc.flox.dataSource.action.Action;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 模板渲染上下文
 *
 * @author cc
 * @date 2024/4/12
 */

@Getter
public class TemplateRenderContext {

    /**
     * 动作
     */
    private final Action action;

    /**
     * 占位符表
     */
    private final Map<String, Object> param;

    /**
     * 渲染后SQL
     */
    @Setter
    private String renderedSQL;

    /**
     * 渲染后参数
     */
    @Setter
    private  Map<String, Object> renderedParam;

    public TemplateRenderContext(Action action, Map<String, Object> param) {
        this.action = action;
        this.param = param;
    }
}