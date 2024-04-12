package com.cc.flox.dataSource.template.impl;

import com.cc.flox.dataSource.template.TemplateType;
import com.cc.flox.dataSource.template.TemplateRenderContext;
import com.cc.flox.dataSource.template.TemplateRenderExecutor;
import org.springframework.stereotype.Component;

/**
 * 普通 sql 渲染执行器
 *
 * @author cc
 * @date 2024/4/12
 */
@Component
public class SqlRenderExecutor implements TemplateRenderExecutor {

    @Override
    public TemplateRenderContext invoke(TemplateRenderContext context) {
        context.setRenderedSQL(context.getAction().getSql());
        context.setRenderedParam(context.getParam());
        return context;
    }

    @Override
    public boolean match(TemplateRenderContext context) {
        return TemplateType.SQL.equals(context.getAction().getTemplateType());
    }
}
