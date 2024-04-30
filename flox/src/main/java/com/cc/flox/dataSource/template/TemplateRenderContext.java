package com.cc.flox.dataSource.template;

import com.cc.flox.dataSource.DataSourceType;
import com.cc.flox.dataSource.action.Action;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
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
     * 数据源类型
     */
    private final DataSourceType dataSourceType;

    /**
     * 渲染后SQL
     */
    @Setter
    private String renderedSQL;

    /**
     * 渲染后参数
     */
    @Setter
    private List<Object> renderedParam;

    /**
     * 是否自定义绑定
     */
    @Setter
    private boolean customBind = false;

    /**
     * 自定义绑定参数
     */
    @Setter
    private Map<String, Object> customBindParam;

    public TemplateRenderContext(Action action, Map<String, Object> param, DataSourceType dataSourceType) {
        this.action = action;
        this.param = param;
        this.dataSourceType = dataSourceType;
    }
}
