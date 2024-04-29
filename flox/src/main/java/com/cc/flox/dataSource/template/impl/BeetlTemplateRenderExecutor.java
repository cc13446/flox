package com.cc.flox.dataSource.template.impl;

import com.cc.flox.dataSource.template.TemplateRenderContext;
import com.cc.flox.dataSource.template.TemplateRenderExecutor;
import com.cc.flox.dataSource.template.TemplateType;
import org.springframework.stereotype.Component;

/**
 * beetl sql 模板
 *
 * @author cc
 * @date 2024/4/29
 */
@Component
public class BeetlTemplateRenderExecutor implements TemplateRenderExecutor {
    @Override
    public TemplateRenderContext invoke(TemplateRenderContext templateRenderContext) {
        return null;
    }

    @Override
    public boolean match(TemplateRenderContext templateRenderContext) {
        return TemplateType.Beetl.equals(templateRenderContext.getAction().getTemplateType());
    }
}
